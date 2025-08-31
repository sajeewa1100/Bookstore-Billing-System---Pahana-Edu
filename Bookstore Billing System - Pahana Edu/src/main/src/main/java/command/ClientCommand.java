package command;



public interface ClientCommand {
    CommandResult execute();
    String getCommandName();
}