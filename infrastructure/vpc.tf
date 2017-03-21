module "vpc" {
  source = "github.com/terraform-community-modules/tf_aws_vpc?ref=v1.0.3"

  name = "karma-tracker"

  cidr            = "${var.vpc_cidr}"
  private_subnets = ["${var.vpc_private_subnets}"]
  public_subnets  = ["${var.vpc_public_subnets}"]

  enable_dns_hostnames = "true"
  enable_dns_support   = "true"
  enable_nat_gateway   = "true"

  azs = ["${var.availability_zones}"]
}
