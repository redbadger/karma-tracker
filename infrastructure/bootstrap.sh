#!/bin/sh -e

export AWS_DEFAULT_PROFILE=karma-tracker/deployment

if ! aws s3api head-bucket --bucket karma-tracker-terraform-state >/dev/null 2>&1; then
  echo "Creating S3 bucket to store state"
  aws s3api create-bucket --bucket karma-tracker-terraform-state \
                          --create-bucket-configuration LocationConstraint=eu-west-1
fi

aws s3api put-bucket-versioning --bucket karma-tracker-terraform-state \
                                --versioning-configuration Status=Enabled

echo "Initialising Terraform backend"
terraform init --backend=true
