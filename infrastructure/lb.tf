resource "aws_alb" "main" {
  name            = "ecs"
  subnets         = ["${module.vpc.public_subnets}"]
  security_groups = ["${aws_security_group.lb.id}"]
}

resource "aws_alb_listener" "http" {
  load_balancer_arn = "${aws_alb.main.id}"
  port              = "80"
  protocol          = "HTTP"

  default_action {
    target_group_arn = "${aws_alb_target_group.ui.id}"
    type             = "forward"
  }
}

resource "aws_alb_listener" "https" {
  load_balancer_arn = "${aws_alb.main.id}"
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = "${var.certificate}"

  default_action {
    type             = "forward"
    target_group_arn = "${aws_alb_target_group.ui.arn}"
  }
}

resource "aws_alb_listener_rule" "api" {
  listener_arn = "${aws_alb_listener.https.arn}"
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = "${aws_alb_target_group.api.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/api/*"]
  }
}

resource "aws_alb_target_group" "ui" {
  name     = "ui"
  port     = 80
  protocol = "HTTP"
  vpc_id   = "${module.vpc.vpc_id}"

  health_check = {
    path = "/health-check"
  }
}

resource "aws_alb_target_group" "api" {
  name     = "api"
  port     = 8000
  protocol = "HTTP"
  vpc_id   = "${module.vpc.vpc_id}"

  health_check = {
    path = "/health-check"
  }
}
