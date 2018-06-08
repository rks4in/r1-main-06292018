/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Thread that reads from stdout/stderr and converts them to log messages.
 * (Sort of a hack.)
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <pthread.h>
#include <assert.h>
#include "StdioConverter.h"

#ifdef __ANDROID__
#include <android/log.h>
#define LOG(priority, tag, ...) __android_log_print(priority, tag, __VA_ARGS__)
#else
#define LOG(priority, tag, ...)
#endif

#define LOG_TAG "StdioConverter"
#define LOGE(...) ((void)LOG(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))
#define LOGW(...) ((void)LOG(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__))
#define LOGI(...) ((void)LOG(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#define LOGD(...) ((void)LOG(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

#define kFilenoStdout   1
#define kFilenoStderr   2

/*
 * Hold our replacement stdout/stderr.
 */
typedef struct StdPipes
{
  int stdoutPipe[2];
  int stderrPipe[2];
} StdPipes;

#define kMaxLine    512

/*
 * Hold some data.
 */
typedef struct BufferedData
{
  char    buf[kMaxLine+1];
  int     count;
} BufferedData;

#ifndef __ANDROID__


/* on non-android we don't mess with the stdout and stderr */
bool dvmStdioConverterStartup(void)
{
  return true;
}

void dvmStdioConverterShutdown(void)
{
}

#else

static void* stdioConverterThreadStart(void* arg);
static bool readAndLog(int fd, const int logLevel, BufferedData* data, const char* tag);

static bool haltStdioConverter;
static bool stdioConverterReady;
static pthread_mutex_t stdioConverterLock;
static pthread_cond_t stdioConverterCond;
static pthread_t stdioConverterHandle;

/*
 * Crank up the stdout/stderr converter thread.
 *
 * Returns immediately.
 */
bool dvmStdioConverterStartup(void)
{
  StdPipes* pipeStorage;

  haltStdioConverter = false;
  stdioConverterReady = false;
  stdioConverterHandle = 0;

  pthread_mutex_init(&stdioConverterLock, NULL);
  pthread_cond_init(&stdioConverterCond, NULL);

  pipeStorage = (StdPipes*) malloc(sizeof(StdPipes));
  if (pipeStorage == NULL)
  {
    return false;
  }

  if (pipe(pipeStorage->stdoutPipe) != 0)
  {
    LOGW("pipe failed: %s\n", strerror(errno));
    free(pipeStorage);
    return false;
  }

  if (pipe(pipeStorage->stderrPipe) != 0)
  {
    LOGW("pipe failed: %s\n", strerror(errno));
    free(pipeStorage);
    return false;
  }

  if (dup2(pipeStorage->stdoutPipe[1], kFilenoStdout) != kFilenoStdout)
  {
    LOGW("dup2(1) failed: %s\n", strerror(errno));
    free(pipeStorage);
    return false;
  }
  close(pipeStorage->stdoutPipe[1]);
  pipeStorage->stdoutPipe[1] = -1;

  if (dup2(pipeStorage->stderrPipe[1], kFilenoStderr) != kFilenoStderr)
  {
    LOGW("dup2(2) failed: %d %s\n", errno, strerror(errno));
    free(pipeStorage);
    return false;
  }
  close(pipeStorage->stderrPipe[1]);
  pipeStorage->stderrPipe[1] = -1;

  /*
   * Create the thread.
   */
  pthread_mutex_lock(&stdioConverterLock);

  if (pthread_create(&stdioConverterHandle,
      NULL, stdioConverterThreadStart, pipeStorage))
  {
    free(pipeStorage);
    LOGW("Failed to create stdio converter thread\n");
    return false;
  }
  /* new thread owns pipeStorage */

  while (!stdioConverterReady)
  {
    int cc = pthread_cond_wait(&stdioConverterCond, &stdioConverterLock);
    (void)cc;
    assert(cc == 0);
  }
  pthread_mutex_unlock(&stdioConverterLock);

  return true;
}

/*
 * Shut down the stdio converter thread if it was started.
 *
 * Since we know the thread is just sitting around waiting for something
 * to arrive on stdout, print something.
 */
void dvmStdioConverterShutdown(void)
{
  haltStdioConverter = true;
  if (stdioConverterHandle == 0)    // not started, or still starting
  {
    return;
  }

  /* print something to wake it up */
  printf("Shutting down\n");
  fflush(stdout);

  LOGD("Joining stdio converter...\n");
  pthread_join(stdioConverterHandle, NULL);
}

/*
 * Select on stdout/stderr pipes, waiting for activity.
 *
 * DO NOT use printf from here.
 */
static void* stdioConverterThreadStart(void* arg)
{
#define MAX(a,b) ((a) > (b) ? (a) : (b))
  StdPipes* pipeStorage = (StdPipes*) arg;
  BufferedData* stdoutData;
  BufferedData* stderrData;
  int cc;

  /* tell the main thread that we're ready */
  pthread_mutex_lock(&stdioConverterLock);
  stdioConverterReady = true;
  cc = pthread_cond_signal(&stdioConverterCond);
  (void)cc; //This is to fix the compiler warning of unused variable in release build
  assert(cc == 0);
  pthread_mutex_unlock(&stdioConverterLock);

  /*
   * Allocate read buffers.
   */
  stdoutData = (BufferedData*) calloc(1, sizeof(*stdoutData));  // Use calloc instead of malloc to zero memory
  stderrData = (BufferedData*) calloc(1, sizeof(*stderrData));
  stdoutData->count = stderrData->count = 0;

  /*
   * Read until shutdown time.
   */
  while (!haltStdioConverter)
  {
    fd_set readfds;
    int maxFd, fdCount;

    FD_ZERO(&readfds);
    FD_SET(pipeStorage->stdoutPipe[0], &readfds);
    FD_SET(pipeStorage->stderrPipe[0], &readfds);
    maxFd = MAX(pipeStorage->stdoutPipe[0], pipeStorage->stderrPipe[0]);

    fdCount = select(maxFd+1, &readfds, NULL, NULL, NULL);

    if (fdCount < 0)
    {
      if (errno != EINTR)
      {
        LOGE("select on stdout/stderr failed\n");
        break;
      }
      LOGD("Got EINTR, ignoring\n");
    }
    else if (fdCount == 0)
    {
      LOGD("WEIRD: select returned zero\n");
    }
    else
    {
      bool err = false;
      if (FD_ISSET(pipeStorage->stdoutPipe[0], &readfds))
      {
        err |= !readAndLog(pipeStorage->stdoutPipe[0], ANDROID_LOG_INFO,
            stdoutData, "NavCorestdout");
      }

      if (FD_ISSET(pipeStorage->stderrPipe[0], &readfds))
      {
        err |= !readAndLog(pipeStorage->stderrPipe[0], ANDROID_LOG_ERROR,
            stderrData, "NavCorestderr");
      }

      /* probably EOF; give up */
      if (err)
      {
        LOGW("stdio converter got read error; shutting it down\n");
        break;
      }
    }
  }

  close(pipeStorage->stdoutPipe[0]);
  close(pipeStorage->stderrPipe[0]);

  free(pipeStorage);
  free(stdoutData);
  free(stderrData);

  return NULL;
#undef MAX
}

/*
 * Data is pending on "fd".  Read as much as will fit in "data", then
 * write out any full lines and compact "data".
 */
static bool readAndLog(int fd, const int logLevel, BufferedData* data, const char* tag)
{
  // Ensure that there is some place to read more data
  assert(data->count < kMaxLine);

  size_t want    = kMaxLine - data->count;
  ssize_t actual = read(fd, data->buf + data->count, want);
  if (actual <= 0)
  {
    LOGW("read %s: (%d,%zu) failed (%ld): %s\n", tag, fd, want, actual, strerror(errno));
    return false;
  } 
  data->count += actual;
  assert(data->count <= kMaxLine);

  const char* end = data->buf + data->count;
  char* remaining = data->buf;
  char* next = data->buf;
  for (char* current = next; current < end; current = next)
  {
    // Look for an EOL.  We expect LF or CRLF, but will try to handle a standalone CR.
    for (; current < end; current++)
    {
      // check LF
      if (*current == '\n')
      {
        remaining = current + 1;
        break;
      }
      // check CR..
      if (*current == '\r')
      {
        remaining = current + 1;
        // check CRLF
        if (((current+1) < end) && (*(current+1) == '\n'))
        {
          remaining++;
        }
        break;
      }
    }
    // NOTE: buffer contains one extra character for null-terminator (see StdioConverter.h)
    assert(current <= end);
    *current = '\0';

    // NOTE: if (current == end) means no EOL found and possible overflow
    if (current < end)
    {
      LOG(logLevel, tag, "%s", next);
      next = remaining;
    }
    else if (data->count < kMaxLine)
    {
      // No EOL found, buffer not full: someone forgot to include '\n'. Just print it.
      LOG(logLevel, tag, "%s!", next);
      remaining = current;
      break;
    }
    else if (next > data->buf)
    {
      // No EOL found and buffer is full: since we at least printed something,
      // which frees up space in the buffer, we'll wait for the next cycle.
      remaining = next;
      break;
    }
    else
    {
      // Overflow: since we did not print anything, print what we have and truncate it.
      LOG(logLevel, tag, "%s!!", next);
      remaining = current;
      break;
    }
  }

  assert((end - remaining) >= 0);
  data->count = end - remaining;

  // if there is data left in buffer, move it to start of buffer
  if (data->count > 0)
  {
    memmove(data->buf, remaining, data->count);
  }

  return true;
}

#endif
