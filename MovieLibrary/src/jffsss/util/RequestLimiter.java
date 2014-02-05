package jffsss.util;

public class RequestLimiter {
	
	int max_requests;		
	long last_request = 0; 	//execution time of the last request in "virtual queue" - may be in past or future
	long delay;				//minimum delay time between each request to guarantee not exceeding limit
							
	
	public RequestLimiter(int max_requests_per_second) 
	{
		this.max_requests = max_requests_per_second;
		this.delay = (1000 / this.max_requests)+10;
	}
	
	public synchronized long getWaitTime()
	{
		long wait_time;
		long current_time = System.currentTimeMillis();
		if ((last_request+delay) <= current_time)
		{
			wait_time = 1;
			last_request = current_time;
		}
		else
		{
			wait_time = (last_request+delay) - current_time;
			last_request = last_request + delay;
		}
			
			
		return wait_time;
	}

}
