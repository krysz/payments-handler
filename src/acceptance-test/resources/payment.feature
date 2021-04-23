Feature: Payments operations
  As a client of this microservice
  I want to browse and modify payments

  Scenario: Get all existing payments
    When call Payment-Handler in order to get all payments from storage
    Then Payment-Handler respond successfully with body and HATEOAS links

  Scenario: Get specific existing payments
    When call Payment-Handler in order to get payment with id 4 from storage
    Then Payment-Handler respond successfully with body and HATEOAS links with one record with id 4

  Scenario: Get non-existing payments
    When call Payment-Handler in order to get payment with id 999 from storage
    Then Payment-Handler respond with 404 not found
