# StockAlert

This is a service which would send out SMS when the current market price of your stock reaches either the lower limit or upper limit.

Redis 

All the stock and its limits are stored in redis under the bucket name alertData

	hset alertData INDIGO '{"name":"INDIGO","lowerLimit":"950","upperLimit":"1050"}'
	hset alertData WOCKPHARMA '{"name":"WOCKPHARMA","lowerLimit":"905","upperLimit":"963"}'

	More information on Redis - http://redis.io/
	
Google Finance API

The API returns an array of object of live stock data.

	 http://www.google.com/finance/info?q=NSE   // Change your Stock Exchange name as per your need
	 
SMS Service

Textlocal provides an API for message delivery
Create an account in textlocal and get the required data
	
	String user = "username=" + "< email-id >";  // your email-id
	String hash = "&hash=" + "< hash value >";   // hash from your textlocal account
	String message = "&message=" + messageToSend;
	String sender = "&sender=" + "TXTLCL";
	String numbers = "&numbers=" + "< mobile number >";   // mobile number to which message needs to be send
	
	More information on Textlocal - https://www.textlocal.in/

Compile

	mvn clean compile assembly:single

Run

	java -jar target/stock-alert-1.0-jar-with-dependencies.jar src/main/resources/config.yml

Add watch command - see live data on your terminal

	watch -d=cumulative -n 20 java -jar target/stock-alert-1.0-jar-with-dependencies.jar src/main/resources/config.yml
	





