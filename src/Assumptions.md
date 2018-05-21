
AccountService:
The service will only be used as a stand alone entity and not over HTTP, in which case a RESTful Spring Boot implementation would be better suited and would
offer persistence, almost out of the box.

AtmService:
No mention of a deposit function in the specification, assume that this indeed correct, although I would enquire as to why this has been left out.  It would
logically go hand in hand with the withdraw and check balance functionality.

General:
Only one user will engage with the service so no need for use of threads and all the concurrency issues that one then has to be cognisant of.