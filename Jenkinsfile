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

@Library('cs-delivery-pipeline@1.0.7') _

import com.tomtom.cs.deliverypipeline.stages.commitstage.bitbucket.CommitStage

DOCKER_IMAGE_PATH = 'cs-fca-r1-docker.navkit-pipeline.tt3.com/tomtom/android-x86_64-toolchain:0.0.6'

pipeline {
  agent {
    label 'Linux' 
  }
  options { 
    timestamps()
    disableConcurrentBuilds()
  }

  stages {
    stage("Commit") {
      steps {
        script {

          def commitStage = new CommitStage(this, "${WORKSPACE}/gradle.properties")
          commitStage.setBuildStep({ instance ->
            buildComponent("clean")
            buildComponent("assemble")
            buildComponent("test")
            buildComponent("publish")
          })
          commitStage.run()
        }
      }
    }
  }
}

def buildComponent(buildArgs) {
  echo "Building buildComponent for '${buildArgs}'"
  sh(script: "docker run --rm \
                         --net=host \
                         -e USER=\$(id -u) \
                         -e _JAVA_OPTIONS=\"-Duser.home=/tmp\" \
                         -w \${WORKSPACE} \
                         -v \${WORKSPACE}:\${WORKSPACE}:rw \
                         -v /tmp:/tools/devenv:ro \
                         -v /etc/group:/etc/group:ro \
                         -v /etc/passwd:/etc/passwd:ro \
                         -u \$(id -u):\$(id -g) \
                         ${DOCKER_IMAGE_PATH} \
                         ./gradlew ${buildArgs}")
}
