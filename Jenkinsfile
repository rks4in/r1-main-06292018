/* Copyright (c) 2018 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 *
 */

@Library('cs-delivery-pipeline@2.1.5') _

import com.tomtom.cs.deliverypipeline.stages.commitstage.bitbucket.CommitStage
import com.tomtom.cs.deliverypipeline.stages.qualitystages.protex.ProtexStage
import com.tomtom.cs.deliverypipeline.stages.qualitystages.coverity.CoverityStage
import com.tomtom.cs.deliverypipeline.stages.qualitystages.tiobetics.TiobeTicsStage

DOCKER_IMAGE_PATH = 'cs-fca-r1-docker.navkit-pipeline.tt3.com/tomtom/android-x86_64-toolchain'

def wasMerged = false

pipeline {
  agent {
    label 'Linux'
  }
  options {
    timestamps()
  }

  parameters {
    choice(name: 'MODALITY',
            choices: 'NORMAL\nUPDATE_DEPENDENCY_MANIFEST\nPROTEX',
            description: 'Modality of this execution of the pipeline.')
    string(name: 'VERSION', defaultValue: '', description: 'Version Tag for Protex')
  }
  triggers {
    parameterizedCron(BRANCH_NAME == "master" ? "H 7-19 * * 1-5 % MODALITY=UPDATE_DEPENDENCY_MANIFEST" : "")
  }

  stages {
    stage("Commit") {
      when {
        expression { (params.MODALITY == 'NORMAL' && BRANCH_NAME != "master")  || params.MODALITY == 'UPDATE_DEPENDENCY_MANIFEST' }
      }
      steps {
        lock(resource: 'LOCK_RESOURCE_FOR_MAIN')
        {
          script {
            if (params.VERSION != '') {
              error "COMMIT STAGE CANNOT HAVE VERSION"
            }
            def commitStage = new CommitStage(this,
                                              "${WORKSPACE}/revision.txt",
                                              "${WORKSPACE}/dependencies.lock",
                                              params.MODALITY as CommitStage.Modality)
            commitStage.addVerificationStep("Build", { instance ->
              callGradleInDocker("clean assemble test")
              stashArtifactsOnMaster()
              unStashArtifactsAndRunTestsOnSlave()
            })
            commitStage.setUpdateDependencyManifestStep({ instance ->
              callGradleInDocker("generateGlobalLock saveGlobalLock")
            })
            commitStage.setPublishStep({ instance ->
              createReleaseCandidate(sh(script: "cat revision.txt", returnStdout: true).trim())
              def artifacts = sh(script: "find ${getArtifactsPublishDirName()} -type f", returnStdout: true).split('\\n')
              artifacts.each {artifact -> addToReleaseCandidate(sh(script: "cat revision.txt", returnStdout: true).trim(), artifact)}
            })
            commitStage.run()

            wasMerged = commitStage.wasMerged()
          }
        }
      }
    }

    stage("Protex Stage") {
      when {
        expression { ((BRANCH_NAME == 'master') && (params.MODALITY == 'PROTEX')) ||
                     ((params.MODALITY == 'NORMAL') && wasMerged) }
      }
      steps {
        lock(resource: 'LOCK_RESOURCE_FOR_PROTEX')
        {
          script {
            if (params.MODALITY != 'PROTEX' && params.VERSION != '') {
              error "MODALITY SHOULD BE \"PROTEX\" TO GIVE VERSION AS A PARAMETER"
            }
            def protexProjectId = 'c_cs_r1_main_8791'
            def protexStage = new ProtexStage(steps: this,
                    secretId: 'protex_creds',
                    projectId: protexProjectId,
                    generateReports: true
                    )
            def revision = ""

            //Cleaning up the repository to only analyze R1 source code
            callGradleInDocker("clean")

            if (params.VERSION != '') {
              //Run Protex on specific revision
              revision = params.VERSION.trim()
            }
            else {
              //After commit is merged, the version number is bumped up.
              //Take the content of previous revision.txt
              revision = sh(script: "git show HEAD~1:revision.txt", returnStdout: true).trim()
            }

            if (BRANCH_NAME == 'master') {
              sh(script: "git fetch")
            }

            if (revision.contains("-SNAPSHOT")) {
              error "VERSION CANNOT HAVE \"SNAPSHOT\" SUFFIX"
            }

            sh(script: "git checkout ${revision}")

            protexStage.run()

            def protexReports = protexStage.getGeneratedReportPaths()

            sh(script: "git checkout master")

            protexReports.each { path -> addToReleaseCandidate(revision, path)}
          }
        }
      }
    }

    stage('Coverity') {
       when {
        expression { ((params.MODALITY == 'NORMAL') && wasMerged) }
       }
      steps {
        lock(resource: 'LOCK_RESOURCE_FOR_COVERITY')
        {
          script {
            withCredentials([usernamePassword(credentialsId: 'tt_service_account_creds', usernameVariable: 'COVUSER', passwordVariable: 'COVPASS')]) {
              def CoverityConfig = ['--java --template',
                                    '--compiler x86_64-linux-android-g++ --comptype g++ --template',
                                    '--compiler cc1plus --comptype gcc --template' ]
              def toolchainVersion = getToolChainVersion()
              def buildDockerImage = "${DOCKER_IMAGE_PATH}:${toolchainVersion}"
              // We have to set the Djdk.internal.lambda.dumpProxyClasses environmental variable because Coverity Build some how wants this
              // for evaluating lambda functions. Also there is a limitation right now that we cannot run Coverity of both Debug and Release.
              // Hence we get the Coverity Analysis only for Release build.
              def environmentalVar = ["_JAVA_OPTIONS=-Djdk.internal.lambda.dumpProxyClasses=/tmp"]
              def codeanalysis = new CoverityStage(steps: this,
                                     buildDockerImage: "${buildDockerImage}",
                                     covStream: "CS_R1_TICS",
                                     covUser: COVUSER,
                                     covPass: COVPASS,
                                     covConfig: CoverityConfig,
                                     scm: "git",
                                     buildCmd: "./gradlew clean assembleRelease",
                                     buildDir: "${WORKSPACE}",
                                     environmentVars: environmentalVar
                                     )
              codeanalysis.run()
            }
          }
        }
      }
    }

    stage('Tiobe') {
      when {
        expression { ((params.MODALITY == 'NORMAL') && wasMerged) }
      }
      steps {
        lock(resource: 'LOCK_RESOURCE_FOR_TIOEBETICS')
        {
          script {
            // Generating code coverage reports is only required for the Tics stage
            // Clean is required otherwise tests are not re-run using tool
            callGradleInDocker("clean testReleaseUnitTestCoverage")

            def toolchainVersion = getToolChainVersion()
            echo "Toolchain Version is ${toolchainVersion}"
            def tiobeTicsAnalysis = new TiobeTicsStage(steps: this,
                                                      buildDockerImage: "${DOCKER_IMAGE_PATH}:${toolchainVersion}",
                                                      sourceDir: "${WORKSPACE}/",
                                                      tiobeBinary: "Ubuntu14.04-64bit",
                                                      tiobeConfiguration: "r1",
                                                      tiobeProject: "CS_R1-Main",
                                                      )
            tiobeTicsAnalysis.run()
          }
        }
      }
    }
  }

  post {
    always {
      sh(script: "rm -rf ${getArtifactsPublishDirName()}")
    }
  }
}

def getArtifactsPublishDirName() {
  return sh(script: "grep artifactPublishDirName= ${WORKSPACE}/publish.properties | sed 's/^.*=//'", returnStdout: true).trim()
}

def getToolChainVersion(){
  return sh(script: "sed '/com.tomtom.r1:toolchain/,/locked/!d;/locked/q' dependencies.lock | \
                                     grep -Eo \'[0-9]+\\.[0-9]+\\.[0-9]+\'", returnStdout: true).trim()
}

def callGradleInDocker(buildArgs) {
  echo "Calling gradle inside of docker for '${buildArgs}'"
  def toolchainVersion = getToolChainVersion()
  echo "Toolchain Version is ${toolchainVersion}"
  sh(script: "docker run --rm \
                         --net=host \
                         -e USER=\$(id -u) \
                         -e _JAVA_OPTIONS=\"-Duser.home=/tmp\" \
                         -e GRADLE_USER_HOME=/var/gradle \
                         -w \${WORKSPACE} \
                         -v \${WORKSPACE}:\${WORKSPACE}:rw \
                         -v /tmp:/tools/devenv:ro \
                         -v /etc/group:/etc/group:ro \
                         -v /etc/passwd:/etc/passwd:ro \
                         -v \${HOME}:/var/gradle:rw \
                         -u \$(id -u):\$(id -g) \
                         ${DOCKER_IMAGE_PATH}:${toolchainVersion} \
                         ./gradlew ${buildArgs}")
}

def createReleaseCandidate(revision) {
  withCredentials([usernamePassword(credentialsId: 'artifactory_creds',
                  usernameVariable: 'USERNAME',
                  passwordVariable: 'PASSWORD')]) {

    sh(script: "release_mgmt_tool.py -u ${USERNAME} \
                                     -p ${PASSWORD} \
                                     -c cs-fca-r1-product-release-candidates \
                                     -r cs-fca-r1-product-releases \
                                     create-rc ${revision}")
  }
}


def addToReleaseCandidate(revision, sourceDir) {
  withCredentials([usernamePassword(credentialsId: 'artifactory_creds',
                  usernameVariable: 'USERNAME',
                  passwordVariable: 'PASSWORD')]) {

    sh(script: "release_mgmt_tool.py -u ${USERNAME} \
                                     -p ${PASSWORD} \
                                     -c cs-fca-r1-product-release-candidates \
                                     -r cs-fca-r1-product-releases \
                                     add-to-rc ${revision} ${sourceDir}")
  }
}

def stashArtifactsOnMaster() {
  sh "ls -la ${pwd()}"
  stash name: 'builtSources', includes: 'build/artifacts/app/*'
  stash name: 'startUpScript', includes: 'Scripts/*'
  echo "Stashing Done"
}

def unStashArtifactsAndRunTestsOnSlave(){
  def toolchainVersion = getToolChainVersion()
  node {
    label 'Test'
    sh "rm -rf jenkins_artifacts"
    sh "mkdir jenkins_artifacts"
    echo "Directory created"
    dir('jenkins_artifacts'){
      echo "Unstashing"
      unstash 'builtSources'
      unstash 'startUpScript'
      runTests(toolchainVersion)
    }
  }
}

def runTests(toolchainVersion) {
  def userId = sh(script: "id -u", returnStdout: true).trim()
  docker.image("cs-fca-r1-docker.navkit-pipeline.tt3.com/tomtom/android-x86_64-toolchain:${toolchainVersion}")
              .inside("-e USER=${userId} --privileged --group-add=plugdev -v /dev/bus/usb:/dev/bus/usb:rw -v /etc/sudoers:/etc/sudoers:ro")
  {
      sh(script: "sudo /usr/local/android-sdk/platform-tools/adb start-server")
      sh(script: "Scripts/smoke_test.sh")
  }
}
