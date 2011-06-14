/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kayak.ui.rawview;

import com.github.kayak.core.Bus;
import com.github.kayak.core.BusChangeListener;
import com.github.kayak.core.Subscription;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//com.github.kayak.ui.rawview//RawView//EN",
autostore = false)
@TopComponent.Description(preferredID = "RawViewTopComponent",
iconBase="org/freedesktop/tango/16x16/mimetypes/text-x-generic.png", 
persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
public final class RawViewTopComponent extends TopComponent {

    private static final Logger logger = Logger.getLogger(RawViewTopComponent.class.getName());
    private Bus bus;
    private Subscription subscription;
    private RawViewTableModel model;
    
    private BusChangeListener listener = new BusChangeListener() {

        @Override
        public void connectionChanged() {
            
        }

        @Override
        public void nameChanged() {
            setName(NbBundle.getMessage(RawViewTopComponent.class, "CTL_RawViewTopComponent") + " - " + bus.getName());
        }

        @Override
        public void destroyed() {
            close();
        }

        @Override
        public void descriptionChanged() {
            
        }
    };

    public RawViewTopComponent() {
        model = new RawViewTableModel();
        initComponents();
        setName(NbBundle.getMessage(RawViewTopComponent.class, "CTL_RawViewTopComponent"));
        setToolTipText(NbBundle.getMessage(RawViewTopComponent.class, "HINT_RawViewTopComponent"));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jToggleButton1 = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jToolBar1.setRollover(true);
        jToolBar1.setMaximumSize(new java.awt.Dimension(32767, 31));

        org.openide.awt.Mnemonics.setLocalizedText(jToggleButton1, org.openide.util.NbBundle.getMessage(RawViewTopComponent.class, "RawViewTopComponent.jToggleButton1.text")); // NOI18N
        jToggleButton1.setFocusable(false);
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jToggleButton1);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(RawViewTopComponent.class, "RawViewTopComponent.jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        add(jToolBar1);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(RawViewTopComponent.class, "RawViewTopComponent.jLabel1.text")); // NOI18N
        jPanel1.add(jLabel1);

        jTextField1.setText(org.openide.util.NbBundle.getMessage(RawViewTopComponent.class, "RawViewTopComponent.jTextField1.text")); // NOI18N
        jTextField1.setMaximumSize(new java.awt.Dimension(2147483647, 31));
        jPanel1.add(jTextField1);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox1, org.openide.util.NbBundle.getMessage(RawViewTopComponent.class, "RawViewTopComponent.jCheckBox1.text")); // NOI18N
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox1);

        add(jPanel1);

        jTable1.setFont(new java.awt.Font("Monospaced", 0, 14));
        jTable1.setModel(model);
        jTable1.setDoubleBuffered(true);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(30);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(20);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(15);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(160);
        jScrollPane1.setViewportView(jTable1);

        add(jScrollPane1);
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed

        if(jCheckBox1.isSelected()) {
            subscription.setSubscribeAll(Boolean.FALSE);
            model.clear();
            String filterString = jTextField1.getText();
            String[] idStrings = filterString.split("\\s");

            for (int i = 0; i < idStrings.length; i++) {
                try {
                    if (idStrings[i].matches("0x[a-fA-F0-9]+")) {
                        subscription.subscribe(Integer.parseInt(idStrings[i].substring(2), 16));
                    } else if (idStrings[i].matches("[a-fA-F0-9]+")) {
                        subscription.subscribe(Integer.parseInt(idStrings[i], 16));
                    }
                } catch (Exception ex) {
                    logger.log(Level.WARNING, "Error while parsing filter string", ex);
                }
            }
        } else {
            subscription.clear();
            subscription.setSubscribeAll(Boolean.TRUE);
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        if(jToggleButton1.isSelected())
            model.setColorized(true);
        else
            model.setColorized(false);
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        model.clear();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        if(subscription != null && bus != null)
            subscription.Terminate();
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    public void setBus(Bus bus) {
        this.bus = bus;
        setName(NbBundle.getMessage(RawViewTopComponent.class, "CTL_RawViewTopComponent") + " - " + bus.getName());
        bus.addBusChangeListener(listener);

        subscription = new Subscription(model, bus);
        subscription.setSubscribeAll(Boolean.TRUE);
    }
}
