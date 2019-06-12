/* Assignment 3
 * Ngan Campbell
 */


import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
public class SimpleNotePad extends JFrame implements ActionListener{
    private static final int MAX_NUMBER_OF_RECENT_FILES = 5;

    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
    private JMenu editMenu = new JMenu("Edit");
    private JTextPane userDataTextPane = new JTextPane();
    private JMenuItem openMenuItem = new JMenuItem("Open");
    private JMenuItem recentMenuItem = new JMenu("Recent");
    private JMenuItem newFileMenuItem = new JMenuItem("New File");
    private JMenuItem saveFileMenuItem = new JMenuItem("Save File");
    private JMenuItem printFileMenuItem = new JMenuItem("Print File");
    private JMenuItem copyMenuItem = new JMenuItem("Copy");
    private JMenuItem pasteMenuItem = new JMenuItem("Paste");
    private JMenuItem replaceMenuItem = new JMenuItem("Replace");
    private File[] recentlyOpenedFiles = new File[MAX_NUMBER_OF_RECENT_FILES];

    public SimpleNotePad() {
        setTitle("A Simple Notepad Tool");
        buildFileMenu();
        buildEditMenu();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
        add(new JScrollPane(userDataTextPane));
        setPreferredSize(new Dimension(600,600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        pack();
    }

    //create a File menu with open, print, save, new file and recent items in it
    private void buildFileMenu(){
        fileMenu.add(openMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(printFileMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveFileMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(newFileMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(recentMenuItem);

        newFileMenuItem.addActionListener(this);
        newFileMenuItem.setActionCommand("new");
        saveFileMenuItem.addActionListener(this);
        saveFileMenuItem.setActionCommand("save");
        printFileMenuItem.addActionListener(this);
        printFileMenuItem.setActionCommand("print");
        recentMenuItem.addActionListener(this);
        recentMenuItem.setActionCommand("recent");
        openMenuItem.addActionListener(this);
        openMenuItem.setActionCommand("open");
    }

    // create Edit menu with copy, paste and replace items in it
    private void buildEditMenu(){
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.add(replaceMenuItem);

        copyMenuItem.addActionListener(this);
        copyMenuItem.setActionCommand("copy");
        pasteMenuItem.addActionListener(this);
        pasteMenuItem.setActionCommand("paste");
        replaceMenuItem.addActionListener(this);
        replaceMenuItem.setActionCommand("replace");
    }

    // build a list of recent open items
    private void buildRecentMenu(){
        recentMenuItem.removeAll();    //reset the list every time the method is called
        for (int i = 0; i < recentlyOpenedFiles.length; i++) {
            if (recentlyOpenedFiles[i] != null) {
                JMenuItem recentFileMenuItem = new JMenuItem(recentlyOpenedFiles[i].getName());
                recentFileMenuItem.addActionListener(this);
                recentFileMenuItem.setActionCommand("recentFile" + i);
                recentMenuItem.add(recentFileMenuItem);
            }
        }
    }

    private void addFileToRecentlyUsedFilesArray(File newFile) {
        for (int i = 0; i < recentlyOpenedFiles.length-1; i++) {
            // move the most recently opened item to next item
            recentlyOpenedFiles[recentlyOpenedFiles.length-1-i] = recentlyOpenedFiles[recentlyOpenedFiles.length-2-i];
        }
        recentlyOpenedFiles[0] = newFile;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int index = 0;
        switch (e.getActionCommand()) {
            // recently open item on the file menu
            case "recentFile0":
            case "recentFile1":
            case "recentFile2":
            case "recentFile3":
            case "recentFile4":
                index = Integer.parseInt(e.getActionCommand().substring(e.getActionCommand().length()-1));
                openFile(recentlyOpenedFiles[index]);
                break;
            // open item on the file menu
            case "open":
                JFileChooser openFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int returnValue = openFileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = openFileChooser.getSelectedFile();
                    openFile(selectedFile);
                }
                break;
            // new item on the file menu
            case "new":
                userDataTextPane.setText("");
                break;
            // save item on the file menu
            case "save":
                File fileToWrite = null;
                JFileChooser saveFileChooser = new JFileChooser();
                int returnVal = saveFileChooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                    fileToWrite = saveFileChooser.getSelectedFile();
                try {
                    if (fileToWrite == null) {
                        throw new AssertionError();
                    }
                    PrintWriter printWriter = new PrintWriter(new FileWriter(fileToWrite));
                    printWriter.println(userDataTextPane.getText());
                    JOptionPane.showMessageDialog(null, "File is saved successfully...");
                    printWriter.close();
                } catch (IOException ex) {
                    System.out.println(ex.toString());
                    System.out.println("File can't be saved.");
                }
                break;
            // print item on the file menu
            case "print":
                try {
                    PrinterJob pjob = PrinterJob.getPrinterJob();
                    pjob.setJobName("Sample Command Pattern");
                    pjob.setCopies(1);
                    pjob.setPrintable((printGraphics, pf, pageNum) -> {
                        if (pageNum > 0)
                            return Printable.NO_SUCH_PAGE;
                        printGraphics.drawString(userDataTextPane.getText(), 500, 500);
                        paint(printGraphics);
                        return Printable.PAGE_EXISTS;
                    });

                    if (!pjob.printDialog())
                        return;
                    pjob.print();
                } catch (PrinterException pe) {
                    JOptionPane.showMessageDialog(null,
                            "Printer error" + pe, "Printing error",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            // copy item on the edit menu
            case "copy":
                userDataTextPane.copy();
                break;
            // paste item on the edit menu
            case "paste":
                StyledDocument doc = userDataTextPane.getStyledDocument();
                Position position = doc.getEndPosition();
                System.out.println("offset" + position.getOffset());
                userDataTextPane.paste();
                break;
            // replace item on the edit menu
            case "replace":
                String replacedValue = JOptionPane.showInputDialog("Replace or insert with");
                if(replacedValue.equals(userDataTextPane.getSelectedText())){
                    break;
                } else {
                    userDataTextPane.replaceSelection(replacedValue);
                }
                break;
        }
    }

    private void openFile(File fileToBeOpened) {
        String line = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            BufferedReader readFile = new BufferedReader(new FileReader(fileToBeOpened));
            while((line = readFile.readLine()) != null) {
                stringBuffer.append(line + "\n");     //add each line to stringbuffer
            }
            // add content of open text file to JTextPane
            userDataTextPane.setText(stringBuffer.toString());
            readFile.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        addFileToRecentlyUsedFilesArray(fileToBeOpened);
        buildRecentMenu();
    }

    public static void main(String[] args) {
        new SimpleNotePad();
    }
}

