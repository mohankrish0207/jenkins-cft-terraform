provider "aws"
  version = "~> 3.0"
  region = "ap-southeast-1"
}

module "VPC"{
  source = ".modules/VPC"
  VPC_CIDR = var.vpccidr
}
