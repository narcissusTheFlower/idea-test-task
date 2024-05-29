import java.nio.file.Path;

public class Main {
// /home/user/IdeaProjects/test/tickets.json
    public static void main(String[] args){

        if (args.length == 0){
            System.out.println("Point jar to the ticket.json file for it to work!");
            return;
        }
        //Implement proper builder with inner class
        TicketParser.of(Path.of(args[0]))
            .parseJson()
            .calculate()
            .printResults();

    }

}
