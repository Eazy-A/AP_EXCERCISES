package labs.lab9.lab92;

import java.util.*;

// component (interface/abstract class)
interface XMLComponent {
    void addAttribute(String type, String value);

    void print(int indent);
}

// leaf (the basic element)
class XMLLeaf implements XMLComponent {
    private String tag;
    private String text;
    private Map<String, String> attributes = new LinkedHashMap<>();

    public XMLLeaf(String tag, String text) {
        this.tag = tag;
        this.text = text;
    }

    @Override
    public void addAttribute(String type, String value) {
        attributes.put(type, value);
    }

    @Override
    public void print(int indent) {
        String indentation = "    ".repeat(indent);
        System.out.print(indentation + "<" + tag);
        attributes.forEach((k, v) -> System.out.print(" " + k + "=\"" + v + "\""));
        System.out.println(">" + text + "</" + tag + ">");
    }
}

// composite (container which stores both leaves and composites)
class XMLComposite implements XMLComponent {
    private String tag;
    private List<XMLComponent> children = new ArrayList<>();
    private Map<String, String> attributes = new LinkedHashMap<>();

    public XMLComposite(String tag) {
        this.tag = tag;
    }

    public void addComponent(XMLComponent component) {
        children.add(component);
    }

    @Override
    public void addAttribute(String type, String value) {
        attributes.put(type, value);
    }

    @Override
    public void print(int indent) {
        String indentation = "    ".repeat(indent);

        System.out.print(indentation + "<" + tag);
        attributes.forEach((k, v) -> System.out.print(" " + k + "=\"" + v + "\""));
        System.out.println(">");

        for (XMLComponent child : children) {
            child.print(indent + 1);
        }

        System.out.println(indentation + "</" + tag + ">");
    }
}

public class XMLTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();
        XMLComponent component = new XMLLeaf("student", "Trajce Trajkovski");
        component.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        XMLComposite composite = new XMLComposite("name");
        composite.addComponent(new XMLLeaf("first-name", "trajce"));
        composite.addComponent(new XMLLeaf("last-name", "trajkovski"));
        composite.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        if (testCase == 1) {
            component.print(0);
        } else if (testCase == 2) {
            composite.print(0);
        } else if (testCase == 3) {
            XMLComposite main = new XMLComposite("level1");
            main.addAttribute("level", "1");
            XMLComposite lvl2 = new XMLComposite("level2");
            lvl2.addAttribute("level", "2");
            XMLComposite lvl3 = new XMLComposite("level3");
            lvl3.addAttribute("level", "3");
            lvl3.addComponent(component);
            lvl2.addComponent(lvl3);
            lvl2.addComponent(composite);
            lvl2.addComponent(new XMLLeaf("something", "blabla"));
            main.addComponent(lvl2);
            main.addComponent(new XMLLeaf("course", "napredno programiranje"));

            main.print(0);
        }
    }
}
