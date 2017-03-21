# karma-tracker infrastructure

This is [Terraform](https://www.terraform.io/) configuration for karma-tracker.

## Overview

The app is available at https://karma.red-badger.com. It is hosted on AWS in the karma-tracker account, which may be accessed by assuming the role `karma-tracker-admin` from the red-badger account.

There are two services (`ui` and `api`) running on an ECS cluster behind an application load balancer.
An ad-hoc task, `update`, is triggered nightly by a lambda function

The ECS cluster consists of EC2 instances running within the private subnets of our VPC.
The load balancer sits in the public subnets.

Logs are written to [CloudWatch](https://eu-west-1.console.aws.amazon.com/cloudwatch/home?region=eu-west-1#logs:).

Docker images for the services (`karma-tracker/ui` and `karma-tracker/cli`) are stored in ECR.

Data is stored in MongoDB Atlas.
The EC2 instances connect via NAT gateways (because we are using the free tier; a paid plan would allow VPC peering, which would be preferable).

DNS is managed in Route53 in the karma-tracker account, which requires an NS record in the parent red-badger account.
The deployment user is able to assume a role in the parent account to manage that record.

The SSL cert is Amazon-issued and should renew automatically.
We aren't using the top-level wildcard cert from the red-badger account because it's not possible to share certs between accounts.

## Setup

Install AWS CLI and Terraform:
```console
$ brew install awscli terraform
```

Note that Terraform 0.9+ is required.
Check your version:
```console
$ terraform -version
Terraform v0.9.1
```

Configure the deployment user for the karma-tracker AWS account:
```console
$ aws configure --profile karma-tracker/deployment
AWS Access Key ID [None]: AKIAIOSFODNN7EXAMPLE
AWS Secret Access Key [None]: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
Default region name [None]: eu-west-1
Default output format [None]:
```

Download Terraform modules:
```console
$ terraform get
```

Log in to the Docker registry:
```console
$ eval $(aws ecr get-login --profile karma-tracker/deployment)
```

The Terraform state is shared in S3, which relies on the existence of an S3 bucket.
This was created by running `./bootstrap.sh`, but hopefully you won't need to do that again.

## Usage

### CLI Docker image

From the root of the repository:
```console
$ scripts/build.sh [version]
$ scripts/push.sh [version]
```

`version` defaults to `latest`.

### UI Docker image

From the `ui` directory:
```console
$ yarn build [version]
$ yarn push [version]
```

`version` defaults to `latest`.

### Deployment

```console
$ terraform plan -var version=[version]
$ terraform apply -var version=[version]
```

Note that when reusing tags (e.g. `latest`), you will need to manually stop the tasks in ECS so that the service launches new tasks with the new images.
For this reason it would be better to use a unique version for each new image (e.g. Git commit hash, CI build number).

### Tedious manual steps :(

If you modify the NAT gateways (this shouldn't generally be the case after the initial deployment), you'll need to manually whitelist the Elastic IPs to allow access to MongoDB.
Grab the IPs from [AWS](https://eu-west-1.console.aws.amazon.com/vpc/home?region=eu-west-1#NatGateways) and whitelist them in [MongoDB](https://cloud.mongodb.com/v2/58c6bb3297019977decd1824#clusters/security/whitelist).

## Making changes

Please run `terraform validate` and `terraform fmt` before committing changes.
