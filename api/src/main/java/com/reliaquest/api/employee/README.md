Feels a bit weird that we would be doing the filtering client side, that's a lot of results being sent back that might never get looked at...

Also, I've never used an API where the client was a Spring controller itself...

I've tried to separate the concerns (i.e. don't couple controller stuff with the actual filtering)


Funny story: I tried to write a test that uses a TestContainer to run the actual server and...my Windows 10 laptop is too old to run Docker *blushes*

The delete on the API seems dodgy; on the client side it says it will accept an id and return their name, on the server side it takes a name and returns a boolean...

I reassessed my next steps, that request limit interceptor is a tricky little so-and-so...how the hell will I create a fair back off policy for a server that will randomly need between 30 and 90 seconds backoff every 5 to 10 attempts!???

Figure my next step has to be to get an integration test working, so I can test...whatever the hell I come up with...end-to-end.