* Third party integration with Expensify. 
* With Expensify, you can track expenses, submit reports, pay bills, generate invoices, and so much more all in one place.
* One can pull vendor info and expenses/payments/bills associated with these vendors from a third party system like expensify, and integrate with their own accounting system.
* This project demonstrates the above. It's a Spring Boot application that pulls data (vendors, vendor transactions) from Expensify, processes and persists the data to a MongoDB instance. The data is pulled and persisted periodically, no manual intervention is necessary.
* https://integrations.expensify.com/Integration-Server/doc/
* https://www.mongodb.com/compatibility/spring-boot

## How to run locally
* [Create](https://www.mongodb.com/cloud/atlas) a MongoDB Atlas cluster.
* Create an `application.properties` file in `src/main/resources` with the following contents.

`spring.data.mongodb.uri=<MongoDB Atlas Cluster URL>`

`spring.data.mongodb.database=<MongoDB Instance name>`

`expensify.baseUrl=https://integrations.expensify.com/Integration-Server/ExpensifyIntegrations`

`expensify.partnerUserID=<Your partner User ID>`

`expensify.partnerUserSecret=<Your partner User Secret>`

`template.path=templates/expensify_template.ftl`

* Run `IntegrationApplication.main()`