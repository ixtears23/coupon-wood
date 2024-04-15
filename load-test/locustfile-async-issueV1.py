import random
from locust import task, FastHttpUser

class HelloWorld(FastHttpUser):
    connection_timeout = 10.0
    network_timout = 10.0

    @task
    def issue(self):
        payload = {
            "userId": random.randint(1, 10000000),
            "couponId": 1,
        }
        self.client.post("/v1/async-issue", json=payload)
