var aws = require('aws-sdk');
var ecs = new aws.ECS({apiVersion: '2014-11-13'});

exports.handler = function(event, context) {
  var params = {
    cluster: process.env.CLUSTER,
    taskDefinition: process.env.TASK_DEFINITION,
    count: 1
  };

  ecs.runTask(params, function (err, data) {
    if (err) {
      console.error('Error while starting task: ' + err);
    } else {
      console.info('Task ' + params.taskDefinition + ' started: ' + JSON.stringify(data.tasks));
    }
  });
};
