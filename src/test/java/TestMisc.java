import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TestMisc
{
    public static void main(String[] args)
    {
        testEscape();
    }

    public static void testEscape()
    {
        String url = "https://www.advocatehealth.com/classes-events/?EventTopicId=29&EventTopicText=Senior%20health&Sea" +
                "rchPattern=Contains&LocationDescendants=true\n\t\t";
//        String url = "ief\n\t";

        StringBuilder builder = new StringBuilder();

        HashSet<Character> escapeCharacters = new HashSet<>();
        escapeCharacters.addAll(Arrays.asList('\t', '\b', '\n', '\r', '\f', '\'', '\"'));

        for (int i = 0; i < url.length(); i++)
        {
            Character c = url.charAt(i);
            if (escapeCharacters.contains(c))
            {
                String nonEscape = String.valueOf(c)
                        .replace("\\", "\\\\")
                        .replace("\n", "\\n")
                        .replace("\t", "\\t")
                        .replace("\r", "\\r")
                        .replace("\b", "\\b")
                        .replace("\f", "\\f");
                builder.append(nonEscape);
            } else {
                builder.append(c);
            }
        }

        System.out.println(builder.toString());
    }


}
