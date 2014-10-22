import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Vector;
import java.awt.Font;

public class Raid2 {

	private JFrame frmRaidVirtualization;
	public JTextField textField;
	public JFileChooser chooser;
	public JList<String> jl;
	public Vector<String> v = new Vector<String>();	//holds directories of raid drives
	public String choosertitle;
	public String currDir = ".";
	public final JButton btnBrowse = new JButton("Browse...");
	public final JButton button = new JButton("Add Disk...");
	public final JButton button_1 = new JButton("RUN");
	private JButton btnEasySetup;
	private JButton button_2;
	public RaidThread[] threads = new RaidThread[4];
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Raid2 window = new Raid2();
					window.frmRaidVirtualization.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Raid2() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	public void disableButtons(){
		button_1.setText("STOP");
		button_1.setBackground(Color.RED);
		btnBrowse.setEnabled(false);
		button.setEnabled(false);
		button_2.setEnabled(false);
		btnEasySetup.setEnabled(false);
		textField.setEnabled(false);
		jl.setEnabled(false);
	}
	
	public void enableButtons(){
		button_1.setText("RUN");
		button_1.setBackground(Color.GREEN);
		btnBrowse.setEnabled(true);
		button.setEnabled(true);
		button_2.setEnabled(true);
		btnEasySetup.setEnabled(true);
		textField.setEnabled(true);
		jl.setEnabled(true);
	}
	
	private void initialize() {
		frmRaidVirtualization = new JFrame();
		frmRaidVirtualization.setTitle("Raid Virtualization");
		frmRaidVirtualization.setBounds(100, 100, 499, 228);
		frmRaidVirtualization.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmRaidVirtualization.setResizable(false);
		frmRaidVirtualization.getContentPane().setLayout(null);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frmRaidVirtualization.setLocation(dim.width/2-frmRaidVirtualization.getSize().width/2, dim.height/2-frmRaidVirtualization.getSize().height/2);

		JLabel lblNewLabel = new JLabel("Primary HD:");
		lblNewLabel.setBounds(10, 11, 72, 14);
		frmRaidVirtualization.getContentPane().add(lblNewLabel);
		
		JLabel lblRaidDrives = new JLabel("Raid Disks:");
		lblRaidDrives.setBounds(10, 69, 72, 14);
		frmRaidVirtualization.getContentPane().add(lblRaidDrives);
		
		jl = new JList<String>(v);
		jl.setBounds(10, 89, 472, 71);
		frmRaidVirtualization.getContentPane().add(jl);

		jl.setPreferredSize(new Dimension(200,200));
		jl.addKeyListener(new KeyAdapter(){				//raid disks deleted -- DEL key
			public void keyPressed(KeyEvent ke) {
				if(ke.getKeyCode()==KeyEvent.VK_DELETE){
					v.remove(jl.getSelectedValue());
					jl.setListData(v);
				}
			}
		});
		
		textField = new JTextField();
		textField.setBounds(82, 8, 302, 20);
		frmRaidVirtualization.getContentPane().add(textField);
		textField.setColumns(10);
		
		btnBrowse.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {		//BROWSE BUTTON ACTION...
					chooser = new JFileChooser();
				    chooser.setCurrentDirectory(new java.io.File("."));
				    chooser.setDialogTitle("choosertitle");
				    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				    chooser.setAcceptAllFileFilterUsed(false);
	
				    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				    	textField.setText(chooser.getSelectedFile().toString());
				    }
			}
		});
		btnBrowse.setBounds(394, 7, 89, 23);
		frmRaidVirtualization.getContentPane().add(btnBrowse);

		button.setFont(new Font("Tahoma", Font.PLAIN, 10));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		//ADD DISK BUTTON ACTION...
				if(v.size() < 4){
					chooser = new JFileChooser();
				    chooser.setCurrentDirectory(new java.io.File(currDir));
				    chooser.setDialogTitle("choosertitle");
				    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				    chooser.setAcceptAllFileFilterUsed(false);
	
				    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				    	v.add(chooser.getSelectedFile().toString());
						jl.setListData(v);
						String path = chooser.getSelectedFile().toString();
						String newPath = path.replace(path.substring(path.lastIndexOf("\\"), path.length()),"");
						currDir = newPath;
						System.out.println(currDir);
				    } else {
				      System.out.println("No Selection ");
				    }
				}
			}
		});
		button.setBounds(269, 60, 105, 23);
		frmRaidVirtualization.getContentPane().add(button);
		
		button_2 = new JButton("Remove Disk...");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				v.remove(jl.getSelectedValue());
				jl.setListData(v);
			}
		});
		button_2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		button_2.setBounds(377, 60, 105, 23);
		frmRaidVirtualization.getContentPane().add(button_2);
		
		button_1.setFont(new Font("Tahoma", Font.BOLD, 10));
		button_1.setBackground(Color.GREEN);
		button_1.setFocusPainted(false);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HashSet<String> hs = new HashSet<String>();
				boolean canRun = true;
				hs.addAll(v);
				hs.clear();
				hs.addAll(v);
				if(hs.size() < v.size() && button_1.getText().equals("RUN")) {	//if there are duplicate raid entries...catch
					System.out.println("Raid folders must be unique.\n");
				} else if(button_1.getText().equals("RUN")){	//else start running
					if(textField.getText().length() < 3) {
						JOptionPane.showMessageDialog(null, "Must specify a legitimate Primary Disk.", "Warning!", JOptionPane.ERROR_MESSAGE);
						canRun = false;
					}
					if(v.size() < 1 && canRun == true) {
						JOptionPane.showMessageDialog(null, "Must specify at least one RAID disk.", "Warning!", JOptionPane.ERROR_MESSAGE);
						canRun = false;
					}
					if(canRun == true){
						int n = JOptionPane.showConfirmDialog(
	                            null, "Contents of raid disks will be modified to match primary drive. DATA MAY BE LOST!",
	                            "Warning!",
	                            JOptionPane.OK_CANCEL_OPTION);
						if (n == JOptionPane.OK_OPTION) {
							disableButtons();
							
							//start raid threads
							for (int i = 0; i < v.size(); i++) {
								threads[i] = new RaidThread(textField.getText(),v.get(i));
								threads[i].start();
							}
						}
					}
				} else {	//code executed on STOP button
					for (int i = 0; i < v.size(); i++) {
						threads[i].stopMe();
					}
					enableButtons();
				}
			}
		});
		button_1.setBounds(252, 171, 72, 23);
		frmRaidVirtualization.getContentPane().add(button_1);
		
		btnEasySetup = new JButton("Simple Setup");
		btnEasySetup.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnEasySetup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String desktopPath = System.getProperty("user.home") + "\\Desktop";
				textField.setText(desktopPath + "\\PrimaryDrive");
				v.clear();
				v.add(desktopPath + "\\RaidDisk1");
				v.add(desktopPath + "\\RaidDisk2");
				v.add(desktopPath + "\\RaidDisk3");
				File f = new File(desktopPath + "\\RaidDisk1");
				if(!f.exists()){
					f.mkdir();
				}
				f = new File(desktopPath + "\\RaidDisk2");
				if(!f.exists()){
					f.mkdir();
				}
				f = new File(desktopPath + "\\RaidDisk3");
				if(!f.exists()){
					f.mkdir();
				}
				f = new File(desktopPath + "\\PrimaryDrive");
				if(!f.exists()){
					f.mkdir();
				}
				jl.setListData(v);
			}
		});
		btnEasySetup.setBounds(137, 171, 105, 23);
		frmRaidVirtualization.getContentPane().add(btnEasySetup);
	}
}
