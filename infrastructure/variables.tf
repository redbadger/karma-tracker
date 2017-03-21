variable "version" {
  description = "Version to deploy"
}

variable "profile" {
  description = "Credentials profile used to connect to AWS"
  default     = "karma-tracker/deployment"
}

variable "region" {
  description = "AWS region in which to create infrastructure"
  default     = "eu-west-1"
}

variable "availability_zones" {
  description = "AWS availability zones in which to create infrastructure"
  default     = ["eu-west-1a", "eu-west-1b"]
}

variable "vpc_cidr" {
  description = "CIDR of VPC"
  default     = "10.0.0.0/16"
}

variable "vpc_private_subnets" {
  description = "CIDRs of VPC private subnets (one per availability zone)"
  default     = ["10.0.0.0/24", "10.0.1.0/24"]
}

variable "vpc_public_subnets" {
  description = "CIDRs of VPC public subnets (one per availability zone)"
  default     = ["10.0.100.0/24", "10.0.101.0/24"]
}

variable "domain" {
  description = "Subdomain of red-badger.com at which to host karma-tracker"
  default     = "karma.red-badger.com"
}

variable "certificate" {
  description = "ARN of the SSL certificate for the domain"
  default     = "arn:aws:acm:eu-west-1:458707459747:certificate/088bd2ec-f975-4cd3-9bf3-8bae14a4cbfd"
}

variable "parent_account_role" {
  description = "ARN of the role that allows modification of DNS records in the red-badger.com root domain"
  default     = "arn:aws:iam::578418881509:role/karma-tracker-deployment"
}

variable "parent_zone_id" {
  description = "ID of the hosted zone for the red-badger.com root domain"
  default     = "Z1AEZBBXFMATMT"
}

variable "ecs_cluster_name" {
  description = "Name of the ECS cluster"
  default     = "karma-tracker"
}

variable "ecs_cluster_instance_type" {
  description = "EC2 instance type of ECS container instances"
  default     = "t2.micro"
}

variable "ecs_cluster_min_size" {
  description = "Minimum number of instances in the ECS cluster"
  default     = 1
}

variable "ecs_cluster_max_size" {
  description = "Maximum number of instances in the ECS cluster"
  default     = 1
}

variable "ecs_service_scaling" {
  description = "Desired number of tasks per ECS service"
  default     = 1
}

variable "ecs_amis" {
  description = "ECS-optimized AMIs (per region)"

  default = {
    us-east-1      = "ami-b2df2ca4"
    us-east-2      = "ami-832b0ee6"
    us-west-1      = "ami-dd104dbd"
    us-west-2      = "ami-022b9262"
    eu-west-1      = "ami-a7f2acc1"
    eu-west-2      = "ami-3fb6bc5b"
    eu-central-1   = "ami-ec2be583"
    ap-northeast-1 = "ami-c393d6a4"
    ap-southeast-1 = "ami-a88530cb"
    ap-southeast-2 = "ami-8af8ffe9"
    ca-central-1   = "ami-ead5688e"
  }
}

variable "ecr_repositories" {
  description = "Docker repositories to hold built images"

  default = [
    "karma-tracker/ui",
    "karma-tracker/cli",
  ]
}

variable "mongodb_username" {
  description = "User with which to connect to MongoDB"
  default     = "karma-tracker"
}

variable "mongodb_password" {
  description = "Password with which to connect to MongoDB"
}

variable "mongodb_uri" {
  description = "MongoDB connection string"
  default     = "karma-tracker-shard-00-00-sqbg5.mongodb.net:27017,karma-tracker-shard-00-01-sqbg5.mongodb.net:27017,karma-tracker-shard-00-02-sqbg5.mongodb.net:27017/karma-tracker?ssl=true&replicaSet=karma-tracker-shard-0&authSource=admin"
}

variable "github_username" {
  description = "User with which to connect to GitHub API"
}

variable "github_token" {
  description = "Token with which to connect to GitHub API"
}
