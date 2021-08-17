resource "aws_s3_bucket" "bucket-name-01" {
  bucket = "bucket-hitech-name-0101"
  acl    = "private"
}

resource "aws_s3_bucket_object" "object"{
  bucket = aws_s3_buket.bucket-name-01.id
  key    = "vpc.yaml"
  source = "/var/lib/jenkins/workspace/Jenkins-terraform-cft-pipeline/modules/vpc/vpc.yaml"
}

resource "aws-cloudformation_stack" "vpccreation" {
  depends_on = [aws_s3_bucket_object.object]
  name       = "CFT-VPC-CREATE"
  disable-rollback = true
  parameter = {
    VPC_CIDR = var.vpccidr	
	}
  templete_url = "https://${aws_s3_bucket.bucket-name-0101.id}.s3-ap-southeast-1.amazonaws.com/vpc.yaml"
}