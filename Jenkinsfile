@Library('common-libs@master') _
try {
    properties([
        parameters([

        ])
    ])
    timeout(time: 20, unit: 'MINUTES') {
		  def nexus_url="http://nexus.dp-common-repository.svc.cluster.local:8081"
		  def sonar_url="http://sonarqube.dp-common-quality.svc.cluster.local:9000"
		  def oc_url="https://api.pri.sit.ncaas.singtelnwk.com:6443"
		  def nexus_repo="dp-naas-commons-maven-snapshots"
                  def ID="DP-NaaS-00"
		  node {
			stage("Initlialize") {
			  project = env.PROJECT_NAME
			}
		  }
		  node("maven") {
			stage("Preparation") {
			  sh "git config --global http.sslVerify false"

				  withCredentials([usernamePassword(credentialsId: 'oclogin', passwordVariable: 'OC_PASSWORD', usernameVariable: 'OC_USERNAME')]) {
ciBuildJQSetup()

						  sh """
								   oc login -u ${OC_USERNAME} -p ${OC_PASSWORD} ${oc_url} --insecure-skip-tls-verify
								   oc project ${project}
								   oc get configmaps settings-mvn -o json | ./jq -r ".data.settings" > ~/.m2/settings.xml
						   """
						   checkout scm
				  }
			}
			stage("Software build") {
      
      // Run the maven build
				sh "mvn -f ./pom.xml -Dmaven.test.skip=true clean package"
			}
    //No Unit test to run

              stage ('NexusIQVulnerabilityScan Analysis') {
                ciBuildNexusIqQualityCheck(NexusIQID: ID)
                    }

			stage('Upload Artifacts'){
				sh "mvn -f ./pom.xml -Dmaven.test.skip=true deploy -Dnexus_host=${nexus_url} -Dnexus_snapshot_repo=${nexus_repo}  -Dsonar.host.url=${sonar_url} -Dsonar.login=60c98634b2c56a9948d0bfbb4e2d1286779368a4"
			} 
		
			}
		}
		
}

 catch (err) {
              echo "in catch block"
              echo "Caught: ${err}"
              currentBuild.result = 'FAILURE'
              throw err
 }
