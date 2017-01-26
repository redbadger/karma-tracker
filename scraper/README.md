# scraper

This is a spike directly searching the [GitHub Archive](https://www.githubarchive.org/) for open-source contributions made by members of a given organisation.

## Setup
You'll need a .lein-env file with credentials for the GitHub API.
Copy .lein-env.example to .lein-env and fill in the blanks.

### GitHub
Create a new [personal access token](https://github.com/settings/tokens) with the `read:org` permission.

## Run

This is currently a command-line application.
You can run it with

```console
$ lein run organisation time-period
```

The `time-period` corresponds to a filename of an hourly dump in the GitHub Archive, for example `2017-01-23-15`.

### Example

To fetch contributions from Badgers for 23 January between 15:00 and 16:00 UTC,

```console
$ lein run redbadger 2017-01-23-15
({:id "5211193985",
  :type "ReleaseEvent",
  :actor
  {:id 19389251,
   :login "badgerbot",
   :display-login "badgerbot",
   :gravatar-id "",
   :url "https://api.github.com/users/badgerbot",
   :avatar-url "https://avatars.githubusercontent.com/u/19389251?"},
  :repo
  {:id 68023701,
   :name "redbadger/website-honestly",
   :url "https://api.github.com/repos/redbadger/website-honestly"},
  :payload
  {:action "published",
   :release
   {:tag-name "1485356429832",
    :tarball-url
    "https://api.github.com/repos/redbadger/website-honestly/tarball/1485356429832",
    :assets-url
    "https://api.github.com/repos/redbadger/website-honestly/releases/5263899/assets",
    :assets [],
    :name "Deployment @ Wed Jan 25 2017 15:00:29",
    :published-at "2017-01-25T15:00:30Z",
    :zipball-url
    "https://api.github.com/repos/redbadger/website-honestly/zipball/1485356429832",
    :author
    {:repos-url "https://api.github.com/users/badgerbot/repos",
     :following-url
     "https://api.github.com/users/badgerbot/following{/other_user}",
     :received-events-url
     "https://api.github.com/users/badgerbot/received_events",
     :gists-url
     "https://api.github.com/users/badgerbot/gists{/gist_id}",
     :avatar-url
     "https://avatars.githubusercontent.com/u/19389251?v=3",
     :type "User",
     :followers-url "https://api.github.com/users/badgerbot/followers",
     :login "badgerbot",
     :gravatar-id "",
     :events-url
     "https://api.github.com/users/badgerbot/events{/privacy}",
     :site-admin false,
     :id 19389251,
     :starred-url
     "https://api.github.com/users/badgerbot/starred{/owner}{/repo}",
     :url "https://api.github.com/users/badgerbot",
     :html-url "https://github.com/badgerbot",
     :organizations-url "https://api.github.com/users/badgerbot/orgs",
     :subscriptions-url
     "https://api.github.com/users/badgerbot/subscriptions"},
    :draft false,
    :id 5263899,
    :prerelease false,
    :url
    "https://api.github.com/repos/redbadger/website-honestly/releases/5263899",
    :html-url
    "https://github.com/redbadger/website-honestly/releases/tag/1485356429832",
    :target-commitish "master",
    :upload-url
    "https://uploads.github.com/repos/redbadger/website-honestly/releases/5263899/assets{?name,label}",
    :body "* compress brie image (#349)",
    :created-at "2017-01-25T14:34:18Z"}},
  :public true,
  :created-at "2017-01-25T15:00:30Z",
  :org
  {:id 265650,
   :login "redbadger",
   :gravatar-id "",
   :url "https://api.github.com/orgs/redbadger",
   :avatar-url "https://avatars.githubusercontent.com/u/265650?"}}
 {:id "5211194007",
  :type "CreateEvent",
  :actor
  {:id 19389251,
   :login "badgerbot",
   :display-login "badgerbot",
   :gravatar-id "",
   :url "https://api.github.com/users/badgerbot",
   :avatar-url "https://avatars.githubusercontent.com/u/19389251?"},
  :repo
  {:id 68023701,
   :name "redbadger/website-honestly",
   :url "https://api.github.com/repos/redbadger/website-honestly"},
  :payload
  {:ref "1485356429832",
   :ref-type "tag",
   :master-branch "master",
   :description "🦄 The Red Badger website. Honestly.",
   :pusher-type "user"},
  :public true,
  :created-at "2017-01-25T15:00:30Z",
  :org
  {:id 265650,
   :login "redbadger",
   :gravatar-id "",
   :url "https://api.github.com/orgs/redbadger",
   :avatar-url "https://avatars.githubusercontent.com/u/265650?"}}
  ...
```
