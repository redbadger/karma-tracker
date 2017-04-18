resource "aws_route53_zone" "main" {
  name = "${var.domain}"
}

resource "aws_route53_record" "main_ns" {
  provider = "aws.parent_account"
  zone_id  = "${var.parent_zone_id}"
  name     = "${var.domain}"
  type     = "NS"
  ttl      = "300"
  records  = ["${aws_route53_zone.main.name_servers}"]
}

resource "aws_route53_record" "lb_alias" {
  zone_id = "${aws_route53_zone.main.zone_id}"
  name    = ""
  type    = "A"

  alias {
    name                   = "${aws_alb.main.dns_name}"
    zone_id                = "${aws_alb.main.zone_id}"
    evaluate_target_health = true
  }
}
