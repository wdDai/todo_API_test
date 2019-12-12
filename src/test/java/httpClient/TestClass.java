package httpClient;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class TestClass extends BaseClass {

    @Test
    public void testSendMailReporting() throws IOException {

        // Arrange
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response;
        HttpGet get = new HttpGet(BASE_URL + "/reporting/mail");

        // Act
        response = client.execute(get);

        // Assert
        int status = response.getStatusLine().getStatusCode();
        assertEquals(status, HttpStatus.SC_OK);
    }
}
