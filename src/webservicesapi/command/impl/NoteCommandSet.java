package webservicesapi.command.impl;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.data.db.IbatisController;
import webservicesapi.data.db.model.note.Note;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Allows recording and displaying of notes. Notes are stored in a database and are persistent.
 * Ibatis is used for object to DB serialization. See the xml configuration file (in the same folder as its associated
 * class) for details.
 *
 * @author Ben Leov
 */
public class NoteCommandSet implements CommandSet {

    private static final Logger logger = LoggerFactory.getLogger(NoteCommandSet.class);

    private IbatisController database;

    public NoteCommandSet(IbatisController database) {
        this.database = database;


        SqlSession session = database.openSession();
        try {
            session.insert("createTableNote");
            session.commit();
            session.close();
        } catch (Exception ex) {
            // exception is thrown if the table already exists. Ignore.
        }


    }

    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String getCommandName() {
                return "note";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue) throws InvalidCommandException {

                String[] params = parameter.split(" ", 2);

                Output out = new Output(this);

                if (params[0].equals("add")) {

                    Note note = new Note((params[1]));
                    SqlSession session = database.openSession();
                    session.insert("insertNote", note);
                    session.commit();
                    session.close();

                    out.addLine("Note added.");

                } else if (params[0].equals("show")) {

                    out.addLine();
                    
                    SqlSession session = database.openSession();
                    List<Note> notes = session.selectList("selectAllNotes");

                    if (notes.size() > 0) {
                        for (Note curr : notes) {
                            out.addLine("NOTE: " + curr.getMessage());
                        }
                    } else {
                        out.addLine("No notes saved.");
                    }

                    out.addLine();

                    session.close();


                } else if (params[0].equals("deleteall")) {
                    // delete notes

                    SqlSession session = database.openSession();
                    session.delete("deleteAllNotes");
                    session.commit();
                    session.close();

                    out.addLine("Notes deleted.");
                } else {
                    throw new InvalidCommandException("Must specify add, show or deleteall");
                }

                queue.send(out);
            }

            @Override
            public String getUsage() {
                return "[add] | [show] | [deleteall]";
            }

            @Override
            public String getHelp() {
                return "Note taking command";
            }

            @Override
            public String[] getRequiredProperties() {
                return null;
            }

        });
        return commands;

    }
}
