public class Sportsman {
    private String name;
    private String team;
    private String position;
    private int height;
    private int weight;
    private double age;

    public Sportsman(String name, String team, String position,
                     int height, int weight, double age) {
        this.name = name;
        this.team = team;
        this.position = position;
        this.height = height;
        this.weight = weight;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getTeam() {
        return team;
    }

    public String getPosition() {
        return position;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public double getAge() {
        return age;
    }

    public void printSportsman(Sportsman sportsman) {
        System.out.println(sportsman.name + ", " + sportsman.team + ", " +
                sportsman.position + ", " + sportsman.height + ", " +
                sportsman.weight + ", " + sportsman.age + ";");
    }
}