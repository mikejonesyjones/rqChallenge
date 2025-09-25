Feels a bit weird that we would be doing the filtering client side, that's a lot of results being sent back that might never get looked at...

Also, I've never used an API where the client was a Spring controller itself...

I've tried to separate the concerns (i.e. don't couple controller stuff with the actual filtering)


Funny story: I tried to write a test that uses a TestContainer to run the actual server and...my Windows 10 laptop is too old to run Docker *blushes*

The delete on the API seems dodgy; on the client side it says it will accept an id and return their name, on the server side it takes a name and returns a boolean...

I reassessed my next steps, that request limit interceptor is a tricky little so-and-so...how the hell will I create a fair back off policy for a server that will randomly need between 30 and 90 seconds backoff every 5 to 10 attempts!???

Figure my next step has to be to get an integration test working, so I can test...whatever the hell I come up with...end-to-end.

So I wrote a test harness thinking it might help me work out what a sensible retry strategy would be...I suppose it did help me realise how wrong my thinking was. Normally I've either been sent a Retry-After header back or else the limits were specified in the API documentation; in any case, it's been me throttling jobs in our code to avoid bombarding other people's APIs. In this exercise, I'm fulfilling my API requests by directly calling another API...

I had the code in front of me all along telling me the server will only handle at best 10 requests every ~30 seconds and worst 5 requests every ~90 seconds. Trying to deal with much more than that except in short bursts is futile, there must come a point where I'm just delaying giving people the inevitable news: 429!

Worst thing I could do is fall over because I just kept accepting requests myself even though the server would never be able to deal with them in any reasonable time.

Something else that is bothering me is how it can be unfair, I might have requests that are waiting to be retried and fresh one could come in and steal its spot causing it to take longer or fail altogether.

I suppose my retry strategy would need to be smarter and have some sort of state tracking how many retries are currently ongoing. Past a certain point, unless I'm willing to make way more attempts, there's no point holding on to them longer.

For example, if I've already got 30 requests waiting, I know that at best I can fulfil those in ~1.5 minutes, if my system is fair and they will complete before the new request, there's no point making the new request retry at all if it isn't going to retry past 90 seconds.

I think I need to stop before I go crazy! This was fun though.