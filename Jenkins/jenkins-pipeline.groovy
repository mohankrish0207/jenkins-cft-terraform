properties([
	
	parameters ([
		string (defaultValue: '', description: 'Enter the AWS account ID', name: 'AWS_Account_ID', trim:false),
		
	])
])	

def awsassumecredential(def acc){
	println acc
	def AWS_ROLE_ARN = 'arn:aws:iam::"+acc +":role/ROLE-JENKINS"
	
	sh"""
	 rm -rf /home/ec2-user/.aws/config
aws sts get-caller-identity
aws sts assume-role --role-session-name Jenkins01 --role-arn  $(AWS_ROLE_ARN} > awscre
"""
sh'''
cat > aws_credentials <<EOF
[default]
output = json
region = ap-southeast-1
aws_access_key_id = $(jq -r .Credentials.AccessKeyId awscre)
aws_secret_access_key = $(jq -r .Credentials.SecretAccessKey awscre)
aws_session_token = $(jq -r .Credentials.SessionToken awscre)
EOF
'''
}

pipeline {
	agent {label 'master'}
	
	stages {
		stage('CLEAN WORKSPACE IN JENKINS SERVER'){
			steps {
				cleanWs()
			}
		}
		
		stage('CLONE THE SOURCE CODE FROM GIT-HUB'){
			steps {
				echo 'In SCM Stage'
				
				git credentialsId: 'a8683da1-277f-4a89-b3fe-2b58c7406dc9', url: 'https://github.com/mohankrish0207/jenkins-cft-terraform.git', branch: 'master' 
			}
		}	
		
		stage('Terraform execution for Creating VPC'){
			steps {
				
				script{
					awsassumecredential(AWS_Account_ID)
				}
				
				sh'''
				
				pwd ls
				
				echo "Terraform init-1"
				terraform init -no-color -compact-warnings
				
				echo "Terraform apply-1"
				terraform apply -auto-approve -target=module.vpc
				
				
				'''
				
				
			}
		}
	}
}