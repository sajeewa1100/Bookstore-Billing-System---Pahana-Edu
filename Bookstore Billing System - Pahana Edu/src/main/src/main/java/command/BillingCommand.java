package command;


public interface BillingCommand {
    CommandResult execute();
    String getCommandName();
}