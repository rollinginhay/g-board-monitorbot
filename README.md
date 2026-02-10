# /g/ MONITOR BOT

A Discord bot that monitor the /g/ board through 4chan's api

## Features

* Schedules requests to 4chan's api, embeds popular threads and post in /g/ in
  your discord server, cache posted embeds to prevent duplicates
* Automatically embeds /g/ links from messages sent in server

## Bot config

### Scopes, permissions, intents

* Scopes: commands, bot
* Permissions: embed links, send messages
* Intent: message content

### Properties

In `application.yml`:

```yaml
bot:
  config:
    token: the Discord bot token
    report-channel: the channel the bot sends embeds in
    thread-rep-threshold: number of replies (not (you)s) to thread to be considered popular
    post-rep-threshold: number of (you)s (backlinks) to post to be considered popular
    schedule-delay: delay between each scheduled report, in milliseconds
```

## Commands

* ping: pong!
* posts: manually trigger report once (does not cache)
* stop: stop scheduled reporting
* start: restart scheduled reporting

## Deployment

Supports Docker deployment.

This app is deployed on Railway. Set REPORT_CHANNEL_ID and DISCORD_TOKEN service variables on Railway UI