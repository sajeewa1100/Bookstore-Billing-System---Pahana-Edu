package command;

public interface BookCommand {
    CommandResult execute();
    String getCommandName();
}
