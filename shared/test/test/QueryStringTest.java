package test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.project.shared.data.StringEncoder;
import com.project.shared.utils.QueryString;

public class QueryStringTest
{
    protected static StringEncoder encoder = new StringEncoder(){
        @Override
        public String encode(String value)
        {
            return value;
        }

        @Override
        public String decode(String value)
        {
            return value;
        }};

    private QueryString createQueryString()
    {
        return QueryString.create(encoder);
    }

    @Test
    public void testSetStringString()
    {
        QueryString queryString = createQueryString();
        queryString.set("a", "1");
        queryString.set("a", "1");
        queryString.set("b", "2");
        queryString.set("a", "1");
        queryString.set("b", "2");

        assertEquals("a=1&b=2", queryString.toString());
    }


    @Test
    public void testAppendStringString()
    {
        QueryString queryString = createQueryString();
        queryString.append("a", "1");
        queryString.append("b", "2");
        queryString.append("b", "3");
        queryString.append("a", "4");

        assertEquals("a=1&a=4&b=2&b=3", queryString.toString());
    }

    @Test
    public void testParse()
    {
        final String query = "a=1&b=2&b=3&a=4";
        QueryString result = QueryString.parse(encoder, query);

        assertEquals("a=1&a=4&b=2&b=3", result.toString());

        assertArrayEquals(new String[] { "1", "4"}, result.getValues("a").toArray());

        assertArrayEquals(new String[] { "2", "3"}, result.getValues("b").toArray());
    }
}
