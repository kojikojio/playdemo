import play.api.test._
import play.api.test.Helpers._
import org.fluentlenium.core.filter.FilterConstructor._
import org.scalatestplus.play.PlaySpec

import org.scalatestplus.play._

class IntegrationSpec extends PlaySpec {
  
  "Application" should {
    
    "work from within a browser" in {
      running(TestServer(3333), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:3333/todos")
        
        browser.$("header h1").first.getText must equal("TODOアプリケーション")
        browser.$("section h1").first.getText must equal("12 todos found")
        
        browser.$("#pagination li.current").first.getText must equal("Displaying 1 to 10 of 12")
        
        browser.$("#pagination li.next a").click()
        
        browser.$("#pagination li.current").first.getText must equal("Displaying 11 to 12 of 12")
        browser.$("#searchbox").text("日")
        browser.$("#searchsubmit").click()
        
        browser.$("section h1").first.getText  must equal("9 todos found")
        browser.$("a", withText("憲法記念日")).click()
        
        browser.$("section h1").first.getText  must equal("Edit todo")

        browser.$("#deadline").text("")
        browser.$("input.primary").click()

        browser.$("section h1").first.getText must equal("12 todos found")
        browser.$(".alert-message").first.getText must equal("Done! Todo 憲法記念日 has been updated")
        
        browser.$("#searchbox").text("日")
        browser.$("#searchsubmit").click()
        
        browser.$("a", withText("憲法記念日")).click()
        browser.$("input.danger").click()

        browser.$("section h1").first.getText must equal("11 todos found")
        browser.$(".alert-message").first.getText must equal("Done! Todo has been deleted")
        
        browser.$("#searchbox").text("日")
        browser.$("#searchsubmit").click()
        
        browser.$("section h1").first.getText must equal("8 todos found")

      }
    }
    
  }
  
}