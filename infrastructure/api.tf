data "template_file" "api_container" {
  template = "${file("containers/api.json")}"

  vars {
    region  = "${var.region}"
    version = "${var.version}"
    command = "api"

    mongodb_uri     = "mongodb://${var.mongodb_username}:${var.mongodb_password}@${var.mongodb_uri}"
    github_username = "${var.github_username}"
    github_token    = "${var.github_token}"
  }
}

resource "aws_ecs_task_definition" "api" {
  family                = "api"
  container_definitions = "${data.template_file.api_container.rendered}"
}

resource "aws_ecs_service" "api" {
  name            = "api"
  cluster         = "${aws_ecs_cluster.default.id}"
  task_definition = "${aws_ecs_task_definition.api.arn}"
  desired_count   = "${var.ecs_service_scaling}"

  iam_role = "${aws_iam_role.ecs_service_role.arn}"

  placement_strategy {
    type  = "spread"
    field = "attribute:ecs.availability-zone"
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.api.id}"
    container_name   = "api"
    container_port   = 8000
  }

  depends_on = ["aws_alb_listener.https"]
}

resource "aws_cloudwatch_log_group" "api" {
  name              = "karma-tracker-api"
  retention_in_days = 1
}
