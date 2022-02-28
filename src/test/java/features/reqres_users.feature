@reqresEndpoints
Feature: Reqres.in - Endpoints feature
  I should be able to interact with Reqres User endpoints

  #URL for Endpoints: https://reqres.in/


  #verification with if else
  @getASpecificUser
  Scenario: I should be able to get a specific user
    Given I perform GET call for a specific user with the id "4"
    When  I see 200 Reqres response status
    Then  I should be able to verify User with the id "4" details

  @getASpecificUserNegativeScenario
  Scenario Outline: I should not be able to get user details with a incorrect user Id
    Given I perform GET call for a specific user with the id "<id>"
    When  I see <status_code> Reqres response status
    Then  I should see a empty response
    Examples:
      | id   | status_code |
      | 52   | 404         |
      | abc  | 404         |
      | -32  | 404         |
      | a!b% | 404         |

    #verification with switch
  @getAListOfUsersOnTheFirstPage
  Scenario: I should be able to get a list of users on a first page
    Given I perform GET call to get a list of users on a page 1
    When  I see 200 Reqres response status
    Then  I should be able to verify User details on a page 1

  @createUpdateAndDeleteUser
  Scenario: I should be able to create a user, then update his details and in the end delete it
    Given I perform POST call to create a user
    And   I see 201 Reqres response status
    And   I should be able to verify a newly created user details
    When  I perform PUT call to update user details for the user
    And   I see 200 Reqres response status
    And   I should be able to verify a updated user details
    And   I perform DELETE call to delete the user
    Then  I see 204 Reqres response status

  @login
  Scenario: I should be able to login
    Given I perform POST call to login with username "eve.holt@reqres.in" and password "cityslicka"
    When  I see 200 Reqres response status
    Then   I should be able to see the static login Token

  @loginNegativeScenario
  Scenario Outline: I should not be able to login with invalid login details
    Given I perform POST call to login with username "<username>" and password "<password>"
    Then  I see 400 Reqres response status
    And   A error message "<error_message>"
    Examples:
      | username            | password       | error_message             |
      | pe!ter@klaven.com   | test123        | user not found            |
      | jane.doe@email.com  | jane321!2      | user not found            |
      | nologin@            | loginPassword  | user not found            |
      |                     | testingNoLogin | Missing email or username |
      | testLogin@reqres.in |                | Missing password          |