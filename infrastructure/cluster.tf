resource "aws_ecs_cluster" "default" {
  name = "${var.ecs_cluster_name}"
}

resource "aws_iam_role" "ecs_instance_role" {
  name = "ecs-instance-role"

  assume_role_policy = "${file("policies/ecs-instance-role.json")}"
}

resource "aws_iam_policy_attachment" "ecs_instance_role_policy" {
  name       = "ecs-instance-role-policy"
  roles      = ["${aws_iam_role.ecs_instance_role.name}"]
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_role" "ecs_service_role" {
  name = "ecs-service-role"

  assume_role_policy = "${file("policies/ecs-service-role.json")}"
}

resource "aws_iam_policy_attachment" "ecs_service_role_policy" {
  name       = "ecs-service-role-policy"
  roles      = ["${aws_iam_role.ecs_service_role.name}"]
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceRole"
}

resource "aws_iam_instance_profile" "ecs" {
  name  = "ecs-instance-profile"
  path  = "/"
  roles = ["${aws_iam_role.ecs_instance_role.name}"]
}

resource "aws_launch_configuration" "ecs" {
  name                 = "ecs"
  image_id             = "${lookup(var.ecs_amis, var.region)}"
  instance_type        = "${var.ecs_cluster_instance_type}"
  security_groups      = ["${aws_security_group.ecs.id}"]
  iam_instance_profile = "${aws_iam_instance_profile.ecs.id}"
  user_data            = "#!/bin/bash\necho ECS_CLUSTER=${aws_ecs_cluster.default.name} > /etc/ecs/ecs.config"
}

resource "aws_autoscaling_group" "ecs" {
  name                 = "ecs"
  vpc_zone_identifier  = ["${module.vpc.private_subnets}"]
  launch_configuration = "${aws_launch_configuration.ecs.name}"
  min_size             = "${var.ecs_cluster_min_size}"
  max_size             = "${var.ecs_cluster_max_size}"
}
