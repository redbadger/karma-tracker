data "template_file" "ui_container" {
  template = "${file("containers/ui.json")}"

  vars {
    region  = "${var.region}"
    version = "${var.version}"
  }
}

resource "aws_ecs_task_definition" "ui" {
  family                = "ui"
  container_definitions = "${data.template_file.ui_container.rendered}"
}

resource "aws_ecs_service" "ui" {
  name            = "ui"
  cluster         = "${aws_ecs_cluster.default.id}"
  task_definition = "${aws_ecs_task_definition.ui.arn}"
  desired_count   = "${var.ecs_service_scaling}"

  iam_role = "${aws_iam_role.ecs_service_role.arn}"

  placement_strategy {
    type  = "spread"
    field = "attribute:ecs.availability-zone"
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.ui.id}"
    container_name   = "ui"
    container_port   = 80
  }

  depends_on = ["aws_alb_listener.https"]
}

resource "aws_cloudwatch_log_group" "ui" {
  name              = "karma-tracker-ui"
  retention_in_days = 1
}
