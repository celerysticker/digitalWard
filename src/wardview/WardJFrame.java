package wardview;

import java.awt.Color;
import patientview.PatientJFrame;
import utility.Patient;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * Ward View displayers all six patients in the ward, along with each patient's
 * live, color-coded pSEWS score, and if applicable, SEWS score and input area
 * for the patient's response to stimulus.
 */

/**
 *
 * @author Christina Lin
 */
public class WardJFrame extends javax.swing.JFrame {
    private Timer timer;
    private int timerSeconds;
    private static final int NUM_PATIENTS = 6;
    private static Patient[] patients;
    private static PatientJFrame[] patientframes;
    private static patientPanel[] panels;
    
    public static final int RESPIRATORY_RATE = 0;
    public static final int SPO2 = 1;
    public static final int SYSTOLIC = 2;
    public static final int HEART_RATE = 3;
    public static final float TEMPERATURE = 0f;
    
    /*
     * Ties together UI components, including buttons, labels, and comboboxes 
     * for a single patient.
     */
    final class patientPanel {
        Patient patient;
        javax.swing.JButton button; 
        javax.swing.JLabel psewsField;
        javax.swing.JLabel sewsField;
        javax.swing.JComboBox comboBox;
        javax.swing.JLabel message;
        
        patientPanel(Patient p, javax.swing.JButton b, javax.swing.JLabel psewsField, 
                javax.swing.JLabel sewsField, javax.swing.JComboBox comboBox, javax.swing.JLabel message) {
            this.patient = p;
            this.button = b;
            this.psewsField = psewsField;
            this.sewsField = sewsField;
            this.comboBox = comboBox;
            this.message = message;
        }
    }
    
    /**
     * Creates new form WardJFrame
     * @throws java.io.IOException
     */
    public WardJFrame() throws IOException {
        initComponents();
        //set window in the center of the screen
        //Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        //Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;
        //Move the window
        this.setLocation(x, y);

        // tie patients to corresponding UI components in patientPanels
        // possible to not hardcode this?
        panels = new patientPanel[NUM_PATIENTS];
        panels[0] = new patientPanel(patients[0], jButton_b1001, psews1001Field, sews1001Field, jComboBox_1001, message1001);
        panels[1] = new patientPanel(patients[1], jButton_b1002, psews1002Field, sews1002Field, jComboBox_1002, message1002);
        panels[2] = new patientPanel(patients[2], jButton_b1003, psews1003Field, sews1003Field, jComboBox_1003, message1003);
        panels[3] = new patientPanel(patients[3], jButton_b1004, psews1004Field, sews1004Field, jComboBox_1004, message1004);
        panels[4] = new patientPanel(patients[4], jButton_b1005, psews1005Field, sews1005Field, jComboBox_1005, message1005);
        panels[5] = new patientPanel(patients[5], jButton_b1006, psews1006Field, sews1006Field, jComboBox_1006, message1006);
        
        // initialize UI fields
        for (int i = 0; i < panels.length; ++i) {
            initPatientPanel(panels[i]);
        }
        
        //set focus on exit button
        jButton_exit.requestFocus();
        
        //set timer
        timerSeconds = 0;
        if (timer == null) {
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // do it every 1 second
                    Calendar cal = Calendar.getInstance();
                    cal.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                    jLabel_systemTime.setText(sdf.format(cal.getTime()));
                    // do it every 5 seconds
                    if (timerSeconds % 5 == 0) {
                        // refresh pSEWS, SEWS, and comboBox
                        for(int i = 0; i < patients.length; ++i) {
                            refreshPatientPanel(panels[i]);
                        }
                    }
                    timerSeconds++;
                }
            });
        }

        if (timer.isRunning() == false) {
            timer.start();
        }
    }
    
    /*
    Initializes UI components for a patient by displaying patient's name, pSEWS
    score with color, and setting comboBox dropdown inactive for low pSEWS.
    */
    private void initPatientPanel(patientPanel pp) {
        Patient p = pp.patient;
        String name = p.getLastName() + ", " + p.getFirstName();
        String buttonLabel = name + " (Bed " + p.getBedNumber() + ")";
        pp.button.setText(buttonLabel);
        int psews = pp.patient.getPSEWS();
        pp.psewsField.setOpaque(true);
        pp.psewsField.setBackground(getColor(psews));
        pp.psewsField.setText(Integer.toString(psews));
        pp.sewsField.setOpaque(true);
        if (psews <= 1) { // low psews, patient does not need attention
            pp.comboBox.setEnabled(false);
        }  
    }
    
    /*
    Updates pSEWS score with color, sets comboBox dropdown active/inactive, and
    updates SEWS score.
    */
    private void refreshPatientPanel(patientPanel pp) {
        // update pSEWS
        int psews = pp.patient.getPSEWS();
        pp.psewsField.setBackground(getColor(psews));
        pp.psewsField.setText(Integer.toString(psews));
        int sews = psews + pp.comboBox.getSelectedIndex();
        pp.sewsField.setText(Integer.toString(sews));
        // update comboBox
        if (psews <= 1) { // green
            pp.comboBox.setEnabled(false);
        }  
        else {
            pp.comboBox.setEnabled(true);
        }
        // update SEWS
        displaySEWS(pp);
    }
    
    /*
    Calculates SEWS from displayed pSEWS, displays SEWS and correspdonding message.
    */
    private void displaySEWS(patientPanel pp) {
        // for consistency, read displayed pSEWS instead of getPSEWS()
        int psews = Integer.parseInt(pp.psewsField.getText());
        int sews = psews + pp.comboBox.getSelectedIndex();
        pp.sewsField.setText(Integer.toString(sews));
        pp.message.setText(getMessage(sews));
    }
    
    /*
    Implements traffic light model for pSEWS scores.
    */
    private Color getColor(int input) {
        if (input <= 1) {
            return Color.green;
        }
        else if (input <= 3) {
            return Color.orange;
        }
        return Color.red;
    }
    
    /*
    Returns recommended course of action based on SEWS score.
    */
    private String getMessage(int sews) {
        if (sews <= 3) {
            return "> Continue routine observation";
        }
        else if (sews <= 5) {
            return ">> Involve the nurse-in-charge immediately";
        }
        return ">>> Call the registrar for immediate review";
    }
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton_b1001 = new javax.swing.JButton();
        jLabel_psews1001 = new javax.swing.JLabel();
        jLabel_sews1001 = new javax.swing.JLabel();
        sews1001Field = new javax.swing.JLabel();
        jComboBox_1001 = new javax.swing.JComboBox();
        psews1001Field = new javax.swing.JLabel();
        jLabel_examinePatient1001 = new javax.swing.JLabel();
        message1001 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton_b1002 = new javax.swing.JButton();
        jLabel_psews1002 = new javax.swing.JLabel();
        jLabel_sews1002 = new javax.swing.JLabel();
        sews1002Field = new javax.swing.JLabel();
        jComboBox_1002 = new javax.swing.JComboBox();
        psews1002Field = new javax.swing.JLabel();
        jLabel_examinePatient1002 = new javax.swing.JLabel();
        message1002 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButton_b1003 = new javax.swing.JButton();
        jLabel_psews1003 = new javax.swing.JLabel();
        jLabel_sews1003 = new javax.swing.JLabel();
        sews1003Field = new javax.swing.JLabel();
        jComboBox_1003 = new javax.swing.JComboBox();
        psews1003Field = new javax.swing.JLabel();
        jLabel_examinePatient1003 = new javax.swing.JLabel();
        message1003 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton_b1004 = new javax.swing.JButton();
        jLabel_psews1004 = new javax.swing.JLabel();
        jLabel_sews1004 = new javax.swing.JLabel();
        sews1004Field = new javax.swing.JLabel();
        jComboBox_1004 = new javax.swing.JComboBox();
        psews1004Field = new javax.swing.JLabel();
        jLabel_examinePatient1004 = new javax.swing.JLabel();
        message1004 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jButton_b1005 = new javax.swing.JButton();
        jLabel_psews1005 = new javax.swing.JLabel();
        jLabel_sews1005 = new javax.swing.JLabel();
        sews1005Field = new javax.swing.JLabel();
        jComboBox_1005 = new javax.swing.JComboBox();
        psews1005Field = new javax.swing.JLabel();
        jLabel_examinePatient1005 = new javax.swing.JLabel();
        message1005 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jButton_b1006 = new javax.swing.JButton();
        jLabel_psews1006 = new javax.swing.JLabel();
        jLabel_sews1006 = new javax.swing.JLabel();
        sews1006Field = new javax.swing.JLabel();
        jComboBox_1006 = new javax.swing.JComboBox();
        psews1006Field = new javax.swing.JLabel();
        jLabel_examinePatient1006 = new javax.swing.JLabel();
        message1006 = new javax.swing.JLabel();
        jLabel_systemTime = new javax.swing.JLabel();
        jButton_exit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(800, 556));
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("MicrosoftSansSerif", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Ward W001");

        jButton_b1001.setText("Patient Name (Bed Number)");
        jButton_b1001.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_b1001ActionPerformed(evt);
            }
        });

        jLabel_psews1001.setText("pSEWS:");

        jLabel_sews1001.setText("SEWS:");

        sews1001Field.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        sews1001Field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sews1001Field.setText("n/a");

        jComboBox_1001.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alert (0)", "Verbal (1)", "Pain (2)", "Unresponsive (3)" }));
        jComboBox_1001.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_1001ActionPerformed(evt);
            }
        });

        psews1001Field.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        psews1001Field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        psews1001Field.setText("n/a");
        psews1001Field.setMaximumSize(new java.awt.Dimension(20, 20));
        psews1001Field.setMinimumSize(new java.awt.Dimension(20, 20));
        psews1001Field.setName(""); // NOI18N

        jLabel_examinePatient1001.setText("Examine Patient:");

        message1001.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        message1001.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_psews1001, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(psews1001Field, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_sews1001, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sews1001Field, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox_1001, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_examinePatient1001)))
                    .addComponent(jButton_b1001)
                    .addComponent(message1001, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(61, 61, 61))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton_b1001, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(message1001)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_examinePatient1001)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel_psews1001)
                            .addComponent(jLabel_sews1001))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(psews1001Field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                            .addComponent(sews1001Field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox_1001, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        jButton_b1002.setText("Patient Name (Bed Number)");
        jButton_b1002.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_b1002ActionPerformed(evt);
            }
        });

        jLabel_psews1002.setText("pSEWS:");

        jLabel_sews1002.setText("SEWS:");

        sews1002Field.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        sews1002Field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sews1002Field.setText("n/a");

        jComboBox_1002.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alert (0)", "Verbal (1)", "Pain (2)", "Unresponsive (3)" }));
        jComboBox_1002.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_1002ActionPerformed(evt);
            }
        });

        psews1002Field.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        psews1002Field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        psews1002Field.setText("n/a");
        psews1002Field.setMaximumSize(new java.awt.Dimension(20, 20));
        psews1002Field.setMinimumSize(new java.awt.Dimension(20, 20));
        psews1002Field.setName(""); // NOI18N

        jLabel_examinePatient1002.setText("Examine Patient:");

        message1002.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_psews1002, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(psews1002Field, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_sews1002, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sews1002Field, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox_1002, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_examinePatient1002)))
                    .addComponent(jButton_b1002)
                    .addComponent(message1002))
                .addGap(61, 61, 61))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton_b1002, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(message1002)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel_sews1002)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sews1002Field, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel_psews1002)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(psews1002Field, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jComboBox_1002, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel_examinePatient1002))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jButton_b1003.setText("Patient Name (Bed Number)");
        jButton_b1003.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_b1003ActionPerformed(evt);
            }
        });

        jLabel_psews1003.setText("pSEWS:");

        jLabel_sews1003.setText("SEWS:");

        sews1003Field.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        sews1003Field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sews1003Field.setText("n/a");

        jComboBox_1003.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alert (0)", "Verbal (1)", "Pain (2)", "Unresponsive (3)" }));
        jComboBox_1003.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_1003ActionPerformed(evt);
            }
        });

        psews1003Field.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        psews1003Field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        psews1003Field.setText("n/a");
        psews1003Field.setMaximumSize(new java.awt.Dimension(20, 20));
        psews1003Field.setMinimumSize(new java.awt.Dimension(20, 20));
        psews1003Field.setName(""); // NOI18N

        jLabel_examinePatient1003.setText("Examine Patient:");

        message1003.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_psews1003, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(psews1003Field, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_sews1003, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sews1003Field, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox_1003, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_examinePatient1003)))
                    .addComponent(jButton_b1003)
                    .addComponent(message1003))
                .addGap(61, 61, 61))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jButton_b1003, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(message1003)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel_sews1003)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sews1003Field, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel_psews1003)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(psews1003Field, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jComboBox_1003, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel_examinePatient1003))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jButton_b1004.setText("Patient Name (Bed Number)");
        jButton_b1004.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_b1004ActionPerformed(evt);
            }
        });

        jLabel_psews1004.setText("pSEWS:");

        jLabel_sews1004.setText("SEWS:");

        sews1004Field.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        sews1004Field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sews1004Field.setText("n/a");

        jComboBox_1004.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alert (0)", "Verbal (1)", "Pain (2)", "Unresponsive (3)" }));
        jComboBox_1004.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_1004ActionPerformed(evt);
            }
        });

        psews1004Field.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        psews1004Field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        psews1004Field.setText("n/a");
        psews1004Field.setMaximumSize(new java.awt.Dimension(20, 20));
        psews1004Field.setMinimumSize(new java.awt.Dimension(20, 20));
        psews1004Field.setName(""); // NOI18N

        jLabel_examinePatient1004.setText("Examine Patient:");

        message1004.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_psews1004, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(psews1004Field, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_sews1004, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sews1004Field, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox_1004, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_examinePatient1004)))
                    .addComponent(jButton_b1004)
                    .addComponent(message1004))
                .addGap(61, 61, 61))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton_b1004, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(message1004)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_examinePatient1004)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel_psews1004)
                            .addComponent(jLabel_sews1004))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(psews1004Field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                            .addComponent(sews1004Field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox_1004, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        jButton_b1005.setText("Patient Name (Bed Number)");
        jButton_b1005.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_b1005ActionPerformed(evt);
            }
        });

        jLabel_psews1005.setText("pSEWS:");

        jLabel_sews1005.setText("SEWS:");

        sews1005Field.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        sews1005Field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sews1005Field.setText("n/a");

        jComboBox_1005.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alert (0)", "Verbal (1)", "Pain (2)", "Unresponsive (3)" }));
        jComboBox_1005.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_1005ActionPerformed(evt);
            }
        });

        psews1005Field.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        psews1005Field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        psews1005Field.setText("n/a");
        psews1005Field.setMaximumSize(new java.awt.Dimension(20, 20));
        psews1005Field.setMinimumSize(new java.awt.Dimension(20, 20));
        psews1005Field.setName(""); // NOI18N

        jLabel_examinePatient1005.setText("Examine Patient:");

        message1005.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_psews1005, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(psews1005Field, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_sews1005, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sews1005Field, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox_1005, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_examinePatient1005)))
                    .addComponent(jButton_b1005)
                    .addComponent(message1005))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton_b1005, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(message1005)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_examinePatient1005)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel_psews1005)
                            .addComponent(jLabel_sews1005))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(sews1005Field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                            .addComponent(jComboBox_1005, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(psews1005Field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton_b1006.setText("Patient Name (Bed Number)");
        jButton_b1006.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_b1006ActionPerformed(evt);
            }
        });

        jLabel_psews1006.setText("pSEWS:");

        jLabel_sews1006.setText("SEWS:");

        sews1006Field.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        sews1006Field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sews1006Field.setText("n/a");

        jComboBox_1006.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alert (0)", "Verbal (1)", "Pain (2)", "Unresponsive (3)" }));
        jComboBox_1006.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_1006ActionPerformed(evt);
            }
        });

        psews1006Field.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        psews1006Field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        psews1006Field.setText("n/a");
        psews1006Field.setMaximumSize(new java.awt.Dimension(20, 20));
        psews1006Field.setMinimumSize(new java.awt.Dimension(20, 20));
        psews1006Field.setName(""); // NOI18N

        jLabel_examinePatient1006.setText("Examine Patient:");

        message1006.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_psews1006, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(psews1006Field, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel_sews1006, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sews1006Field, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox_1006, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_examinePatient1006)))
                    .addComponent(jButton_b1006)
                    .addComponent(message1006))
                .addGap(61, 61, 61))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jButton_b1006, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(message1006)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_examinePatient1006)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel_psews1006)
                            .addComponent(jLabel_sews1006))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(psews1006Field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                            .addComponent(sews1006Field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox_1006, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel_systemTime.setText("TIME");

        jButton_exit.setText("Exit");
        jButton_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_exitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_systemTime, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_exit)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton_exit)
                    .addComponent(jLabel_systemTime))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox_1001ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_1001ActionPerformed
        displaySEWS(panels[0]);
    }//GEN-LAST:event_jComboBox_1001ActionPerformed

    private void jButton_b1001ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_b1001ActionPerformed
        this.setVisible(false);
        patientframes[0].setVisible(true);
    }//GEN-LAST:event_jButton_b1001ActionPerformed

    private void jButton_b1002ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_b1002ActionPerformed
        this.setVisible(false);
        patientframes[1].setVisible(true);
    }//GEN-LAST:event_jButton_b1002ActionPerformed

    private void jComboBox_1002ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_1002ActionPerformed
        displaySEWS(panels[1]);
    }//GEN-LAST:event_jComboBox_1002ActionPerformed

    private void jButton_b1003ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_b1003ActionPerformed
        this.setVisible(false);
        patientframes[2].setVisible(true);
    }//GEN-LAST:event_jButton_b1003ActionPerformed

    private void jComboBox_1003ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_1003ActionPerformed
        displaySEWS(panels[2]);
    }//GEN-LAST:event_jComboBox_1003ActionPerformed

    private void jButton_b1004ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_b1004ActionPerformed
        this.setVisible(false);
        patientframes[3].setVisible(true);
    }//GEN-LAST:event_jButton_b1004ActionPerformed

    private void jComboBox_1004ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_1004ActionPerformed
        displaySEWS(panels[3]);
    }//GEN-LAST:event_jComboBox_1004ActionPerformed

    private void jButton_b1005ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_b1005ActionPerformed
        this.setVisible(false);
        patientframes[4].setVisible(true);
    }//GEN-LAST:event_jButton_b1005ActionPerformed

    private void jComboBox_1005ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_1005ActionPerformed

        displaySEWS(panels[4]);
    }//GEN-LAST:event_jComboBox_1005ActionPerformed

    private void jButton_b1006ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_b1006ActionPerformed
        this.setVisible(false);
        patientframes[5].setVisible(true);
    }//GEN-LAST:event_jButton_b1006ActionPerformed

    private void jComboBox_1006ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_1006ActionPerformed
        displaySEWS(panels[5]);
    }//GEN-LAST:event_jComboBox_1006ActionPerformed

    private void jButton_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_exitActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jButton_exitActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(WardJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WardJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WardJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WardJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Intialize patients */
        String line = "";
        patients = new Patient[NUM_PATIENTS];
        String[] arr;
        
        try (Scanner scanner = new Scanner(new File("data/patientList.csv"))) {
            scanner.useDelimiter(",");
            int i = 0;
            while(scanner.hasNextLine()) {
                line = scanner.nextLine();
                arr = line.split(",");
                String fName = arr[1];
                String lName = arr[2];
                String bedNum = arr[0];
                String gender = arr[3];
                String dob = arr[4];
                if (i > 0) {
                    patients[i-1] = new Patient(1001, Integer.parseInt(bedNum), fName, lName, gender, dob);
                }
                i++;
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            Logger.getLogger(WardJFrame.class.getName()).log(Level.SEVERE, null, e);
        }
        
        // assign input files to patients
        patients[0].setDataFile("data/Alice_Bailey_20141011091022.csv");
        patients[1].setDataFile("data/Charlie_Dean_20141013103445.csv");
        patients[2].setDataFile("data/Elise_Foster_20141013122956.csv");
        patients[3].setDataFile("data/Grace_Hughes_20141013161902.csv");
        patients[4].setDataFile("data/Ian_Jones_20141013142915.csv");
        patients[5].setDataFile("data/Kelly_Lawrence_201410141532.csv");
        
        /* prepare to initialize ward UI */
        patientframes = new PatientJFrame[NUM_PATIENTS];
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    WardJFrame w = new WardJFrame();
                    w.setVisible(true);
                    for (int j = 0; j < patients.length; ++j) {
                        patientframes[j] = new PatientJFrame(patients[j], w);
                    }
                }
                catch (IOException e) {
                    Logger.getLogger(WardJFrame.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_b1001;
    private javax.swing.JButton jButton_b1002;
    private javax.swing.JButton jButton_b1003;
    private javax.swing.JButton jButton_b1004;
    private javax.swing.JButton jButton_b1005;
    private javax.swing.JButton jButton_b1006;
    private javax.swing.JButton jButton_exit;
    private javax.swing.JComboBox jComboBox_1001;
    private javax.swing.JComboBox jComboBox_1002;
    private javax.swing.JComboBox jComboBox_1003;
    private javax.swing.JComboBox jComboBox_1004;
    private javax.swing.JComboBox jComboBox_1005;
    private javax.swing.JComboBox jComboBox_1006;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel_examinePatient1001;
    private javax.swing.JLabel jLabel_examinePatient1002;
    private javax.swing.JLabel jLabel_examinePatient1003;
    private javax.swing.JLabel jLabel_examinePatient1004;
    private javax.swing.JLabel jLabel_examinePatient1005;
    private javax.swing.JLabel jLabel_examinePatient1006;
    private javax.swing.JLabel jLabel_psews1001;
    private javax.swing.JLabel jLabel_psews1002;
    private javax.swing.JLabel jLabel_psews1003;
    private javax.swing.JLabel jLabel_psews1004;
    private javax.swing.JLabel jLabel_psews1005;
    private javax.swing.JLabel jLabel_psews1006;
    private javax.swing.JLabel jLabel_sews1001;
    private javax.swing.JLabel jLabel_sews1002;
    private javax.swing.JLabel jLabel_sews1003;
    private javax.swing.JLabel jLabel_sews1004;
    private javax.swing.JLabel jLabel_sews1005;
    private javax.swing.JLabel jLabel_sews1006;
    private javax.swing.JLabel jLabel_systemTime;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel message1001;
    private javax.swing.JLabel message1002;
    private javax.swing.JLabel message1003;
    private javax.swing.JLabel message1004;
    private javax.swing.JLabel message1005;
    private javax.swing.JLabel message1006;
    private javax.swing.JLabel psews1001Field;
    private javax.swing.JLabel psews1002Field;
    private javax.swing.JLabel psews1003Field;
    private javax.swing.JLabel psews1004Field;
    private javax.swing.JLabel psews1005Field;
    private javax.swing.JLabel psews1006Field;
    private javax.swing.JLabel sews1001Field;
    private javax.swing.JLabel sews1002Field;
    private javax.swing.JLabel sews1003Field;
    private javax.swing.JLabel sews1004Field;
    private javax.swing.JLabel sews1005Field;
    private javax.swing.JLabel sews1006Field;
    // End of variables declaration//GEN-END:variables
}
