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

@Library('cs-delivery-pipeline@1.1.19') _

import com.tomtom.cs.deliverypipeline.stages.commitstage.bitbucket.CommitStage
import com.tomtom.cs.deliverypipeline.stages.qualitystages.protex.ProtexStage

DOCKER_IMAGE_PATH = 'cs-fca-r1-docker.navkit-pipeline.tt3.com/tomtom/android-x86_64-toolchain:0.0.8'

def wasMerged = false

pipeline {
  agent {
    label 'Linux'
  }
  options {
    timestamps()
    disableConcurrentBuilds()
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
        script {
          if (params.VERSION != '') {
            error "COMMIT STAGE CANNOT HAVE VERSION"
          }
          def commitStage = new CommitStage(this,
                                            "${WORKSPACE}/revision.txt",
                                            "${WORKSPACE}/dependencies.lock",
                                            params.MODALITY as CommitStage.Modality)
          commitStage.setBuildStep({ instance ->
            callGradleInDocker("clean assemble test")
          })
          commitStage.setUpdateDependencyManifestStep({ instance ->
              callGradleInDocker("generateGlobalLock saveGlobalLock")
          })
          commitStage.setPublishStep({ instance ->
              createReleaseCandidate(sh(script: "cat revision.txt", returnStdout: true).trim())
              def artifacts = sh(script: "find `grep artifactPublishDirName= ${WORKSPACE}/publish.properties | sed 's/^.*=//'` -type f", returnStdout: true).split('\\n')
              artifacts.each {artifact -> addToReleaseCandidate(sh(script: "cat revision.txt", returnStdout: true).trim(), artifact)}
          })
          commitStage.run()

          wasMerged = commitStage.wasMerged()
        }
      }
    }

    stage("Protex Stage") {
      when {
        expression { ((BRANCH_NAME == 'master') && (params.MODALITY == 'PROTEX')) ||
                     ((params.MODALITY == 'NORMAL') && wasMerged) }
      }
      steps {
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
}

def callGradleInDocker(buildArgs) {
  echo "Calling gradle inside of docker for '${buildArgs}'"
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
                         ${DOCKER_IMAGE_PATH} \
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
