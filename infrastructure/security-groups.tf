resource "aws_security_group" "ecs" {
  name        = "ecs"
  description = "ECS instances"

  vpc_id = "${module.vpc.vpc_id}"
}

resource "aws_security_group_rule" "ecs_allow_all_outgoing" {
  type      = "egress"
  from_port = 0
  to_port   = 0
  protocol  = "-1"

  cidr_blocks = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.ecs.id}"
}

resource "aws_security_group_rule" "ecs_allow_incoming_http" {
  type      = "ingress"
  from_port = 32768
  to_port   = 61000
  protocol  = "tcp"

  source_security_group_id = "${aws_security_group.lb.id}"

  security_group_id = "${aws_security_group.ecs.id}"
}

resource "aws_security_group" "lb" {
  name        = "lb"
  description = "Load balancer"

  vpc_id = "${module.vpc.vpc_id}"
}

resource "aws_security_group_rule" "lb_allow_incoming_http" {
  type      = "ingress"
  from_port = 80
  to_port   = 80
  protocol  = "tcp"

  cidr_blocks = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.lb.id}"
}

resource "aws_security_group_rule" "lb_allow_incoming_https" {
  type      = "ingress"
  from_port = 443
  to_port   = 443
  protocol  = "tcp"

  cidr_blocks = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.lb.id}"
}

resource "aws_security_group_rule" "lb_allow_all_outgoing" {
  type      = "egress"
  from_port = 0
  to_port   = 0
  protocol  = "-1"

  cidr_blocks = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.lb.id}"
}
