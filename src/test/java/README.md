This is a test REST API testing framework for Reqres.in user endpoints.
The framework uses Rest Assured + Cucumber.

The framework consists of:

1. Constants folder which includes Base URI and Endpoints
2. Features folder
3. Steps folder


Endpoints used for testing can be found here: https://reqres.in/

"reqres_users.feature" file consists of different test cases
like getting user details, creating, updating and then deleting user details
and also trying to successfully log in with a specific user details.

Test cases can be executed separately or ran as a whole feature file.

The types of test cases that should be automated have a lot of complex manual scenarios
like, for example user details creation & updating them and deletion scenario, same for 
login test cases with specific details.

To open the project use pom.xml file or import it from the GitHub in your IDE

The framework also consists of a folder "trust_wallet" (TrustWalletHomework->src->test->trust_wallet) which has Trust Wallet
Android App test cases for wallet creation and a Test Plan.
