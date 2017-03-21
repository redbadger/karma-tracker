terraform {
  backend "s3" {
    bucket  = "karma-tracker-terraform-state"
    key     = "terraform.tfstate"
    region  = "eu-west-1"
    profile = "karma-tracker/deployment"
  }
}

provider "aws" {
  profile = "${var.profile}"
  region  = "${var.region}"
}

provider "aws" {
  alias   = "parent_account"
  profile = "${var.profile}"
  region  = "${var.region}"

  assume_role {
    role_arn = "${var.parent_account_role}"
  }
}
