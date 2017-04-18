data "template_file" "update_container" {
  template = "${file("containers/update.json")}"

  vars {
    region  = "${var.region}"
    version = "${var.version}"
    command = "update"

    mongodb_uri     = "mongodb://${var.mongodb_username}:${var.mongodb_password}@${var.mongodb_uri}"
    github_username = "${var.github_username}"
    github_token    = "${var.github_token}"
  }
}

resource "aws_ecs_task_definition" "update" {
  family                = "update"
  container_definitions = "${data.template_file.update_container.rendered}"
}

resource "aws_cloudwatch_log_group" "update" {
  name              = "karma-tracker-update"
  retention_in_days = 7
}

data "archive_file" "update_lambda_source" {
  type        = "zip"
  source_dir  = "lambdas/update"
  output_path = "target/update.zip"
}

resource "aws_iam_role" "update_lambda_role" {
  name = "update-lambda-role"

  assume_role_policy = "${file("policies/update-lambda-role.json")}"
}

data "template_file" "update_lambda_policy" {
  template = "${file("policies/update-lambda-policy.json")}"

  vars {
    task_definition = "${aws_ecs_task_definition.update.arn}"
  }
}

resource "aws_iam_policy" "update_lambda_policy" {
  name   = "update-lambda-policy"
  policy = "${data.template_file.update_lambda_policy.rendered}"
}

resource "aws_iam_policy_attachment" "update_lambda_policy" {
  name       = "update-lambda-policy"
  roles      = ["${aws_iam_role.update_lambda_role.name}"]
  policy_arn = "${aws_iam_policy.update_lambda_policy.arn}"
}

resource "aws_lambda_function" "update" {
  function_name    = "update"
  filename         = "target/update.zip"
  source_code_hash = "${data.archive_file.update_lambda_source.output_base64sha256}"
  role             = "${aws_iam_role.update_lambda_role.arn}"
  runtime          = "nodejs4.3"
  handler          = "index.handler"

  environment {
    variables = {
      CLUSTER         = "${aws_ecs_cluster.default.id}"
      TASK_DEFINITION = "${aws_ecs_task_definition.update.arn}"
    }
  }
}

resource "aws_cloudwatch_event_rule" "update" {
  name        = "update"
  description = "Download events from GitHub and store them in MongoDB"

  schedule_expression = "cron(0 4 * * ? *)"
}

resource "aws_cloudwatch_event_target" "update" {
  rule = "${aws_cloudwatch_event_rule.update.name}"
  arn  = "${aws_lambda_function.update.arn}"
}

resource "aws_lambda_permission" "update_allow_cloudwatch" {
  statement_id  = "allow-update-from-cloudwatch"
  action        = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.update.function_name}"
  principal     = "events.amazonaws.com"
  source_arn    = "${aws_cloudwatch_event_rule.update.arn}"
}
