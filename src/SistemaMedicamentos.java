import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SistemaMedicamentos {

    private static Map<String, Medicamento> medicamentos = new HashMap<>();
    private static Set<String> laboratorios = new HashSet<>();
    private static Set<String> principiosAtivos = new HashSet<>();
    private static Set<String> paraQueServe = new HashSet<>();
    private static Set<String> posologias = new HashSet<>();

    private static final String ARQUIVO_LICENCA = "licenca.txt";
    private static long tempoLicenca; // Tempo de licença em milissegundos
    private static final String SENHA_LICENCA = "210198"; // Senha para acessar a tela de licença

    public static void main(String[] args) {
        carregarDados();
        carregarLicenca(); // Carrega o tempo de licença

        // Verifica se a licença expirou
        if (tempoLicenca <= System.currentTimeMillis()) {
            JOptionPane.showMessageDialog(null, "Licença expirada. Contate o Kaynan.", "Erro", JOptionPane.ERROR_MESSAGE);
            // Não sair do programa, permitir que o usuário altere a licença
        }

        SwingUtilities.invokeLater(SistemaMedicamentos::criarMenuPrincipal);
    }
    private static void fecharTodasAsJanelas() {
        for (Window window : Window.getWindows()) {
            if (window instanceof JFrame) {
                window.dispose(); // Fecha a janela
            }
        }
    }
    // Método para carregar a licença
    private static void carregarLicenca() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_LICENCA))) {
            String linha = reader.readLine();
            if (linha != null) {
                tempoLicenca = Long.parseLong(linha.trim());
            } else {
                // Licença padrão de 5 minutos
                tempoLicenca = System.currentTimeMillis() + (1 * 60 * 1000);
                salvarLicenca();
            }
        } catch (IOException | NumberFormatException e) {
            // Se houver erro, inicia com licença padrão de 5 minutos
            tempoLicenca = System.currentTimeMillis() + (1 * 60 * 1000);
            salvarLicenca();
        }
    }

    // Método para salvar a licença
    private static void salvarLicenca() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_LICENCA))) {
            writer.println(tempoLicenca);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar licença.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Classe para representar um medicamento
    private static class Medicamento {
        String descricao;
        String laboratorio;
        String paraQueServe;
        String posologia;
        String principioAtivo;

        Medicamento(String descricao, String laboratorio, String paraQueServe, String posologia, String principioAtivo) {
            this.descricao = descricao;
            this.laboratorio = laboratorio;
            this.paraQueServe = paraQueServe;
            this.posologia = posologia;
            this.principioAtivo = principioAtivo;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    // Menu principal
    private static void criarMenuPrincipal() {
        JFrame frame = new JFrame("Sistema de Medicamentos");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Impede o fechamento
        frame.setResizable(false); // Impede o redimensionamento
        frame.setUndecorated(true); // Remove a barra de título, impedindo a minimização
        frame.setAlwaysOnTop(true); // Mantém a janela sempre no topo, incluindo sobre a barra de tarefas
        frame.setSize(300, 400); // Tamanho fixo
        frame.setLocation(0, Toolkit.getDefaultToolkit().getScreenSize().height - 400); // Posição fixa na extremidade esquerda inferior

        // Painel com imagem de fundo
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\K-system21\\Sytem21\\imagem de fundo.png"); // Coloque o caminho da sua imagem aqui
                Image img = icon.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        JLabel titulo = new JLabel("Cia dos Animais", JLabel.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 32)); // Fonte menor
        titulo.setForeground(Color.green.darker()); // Texto branco
        titulo.setBorder(BorderFactory.createEmptyBorder(-200, 0, 0, 0)); // (topo, esquerda, baixo, direita)

        panel.add(titulo, BorderLayout.CENTER);

        JPanel botoes = new JPanel();
        botoes.setOpaque(false); // Painel transparente
        botoes.setLayout(new GridLayout(4, 1, 5, 5)); // Reduzindo o espaçamento

        JButton btnCadastro = new JButton("Cadastro de Medicamentos");
        btnCadastro.setFont(new Font("SansSerif", Font.BOLD, 12)); // Fonte menor
        btnCadastro.setBackground(new Color(50, 205, 50)); // Verde claro
        btnCadastro.setForeground(Color.black); // Texto branco
        btnCadastro.addActionListener(e -> verificarSenha());

        JButton btnBusca = new JButton("Buscar Produtos");
        btnBusca.setFont(new Font("SansSerif", Font.BOLD, 12)); // Fonte menor
        btnBusca.setBackground(new Color(50, 205, 50)); // Verde claro
        btnBusca.setForeground(Color.black); // Texto branco
        btnBusca.addActionListener(e -> abrirTelaBusca());

        JButton btnLicenca = new JButton("Licença");
        btnLicenca.setFont(new Font("SansSerif", Font.BOLD, 12)); // Fonte menor
        btnLicenca.setBackground(new Color(50, 205, 50)); // Verde claro
        btnLicenca.setForeground(Color.black); // Texto branco
        btnLicenca.addActionListener(e -> verificarSenhaLicenca());

        JButton btnSair = new JButton("Sair");
        btnSair.setFont(new Font("SansSerif", Font.BOLD, 12)); // Fonte menor
        btnSair.setBackground(new Color(50, 205, 50)); // Verde claro
        btnSair.setForeground(Color.black); // Texto branco
        btnSair.addActionListener(e -> {
            salvarDados();
            System.exit(0);
        });

        // Verifica se a licença expirou e desabilita os botões apropriados
        if (tempoLicenca <= System.currentTimeMillis()) {
            btnCadastro.setEnabled(false);
            btnBusca.setEnabled(false);
        }

        botoes.add(btnCadastro);
        botoes.add(btnBusca);
        botoes.add(btnLicenca);
        botoes.add(btnSair);

        panel.add(botoes, BorderLayout.SOUTH);
        frame.add(panel);
        frame.setVisible(true);
    }

    // Verificação de senha para acessar o cadastro
    private static void verificarSenha() {
        String senhaCorreta = "cia123"; // Senha fixa
        JPasswordField passwordField = new JPasswordField(); // Usando JPasswordField para ocultar o texto
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Fonte menor
        int option = JOptionPane.showConfirmDialog(null, passwordField, "Digite a senha para acessar o cadastro:", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String senha = new String(passwordField.getPassword());
            if (senha.equals(senhaCorreta)) {
                abrirTelaCadastro();
            } else {
                JOptionPane.showMessageDialog(null, "solicite a mudança, você não vai acertar a senha!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Verificação de senha para acessar a tela de licença
    private static void verificarSenhaLicenca() {
        JPasswordField passwordField = new JPasswordField();
        int option = JOptionPane.showConfirmDialog(null, passwordField, "Digite a senha da licença:", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String senha = new String(passwordField.getPassword());
            if (senha.equals(SENHA_LICENCA)) {
                abrirTelaLicenca();
            } else {
                JOptionPane.showMessageDialog(null, "Senha incorreta.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Tela de licença
    private static void abrirTelaLicenca() {
        JFrame licencaFrame = new JFrame("Adicionar Licença");
        licencaFrame.setSize(300, 200);
        licencaFrame.setLocationRelativeTo(null);
        licencaFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblMinutos = new JLabel("Minutos para adicionar:");
        JTextField txtMinutos = new JTextField();
        JButton btnAdicionar = new JButton("Adicionar");

        btnAdicionar.addActionListener(e -> {
            try {
                int minutos = Integer.parseInt(txtMinutos.getText().trim());
                if (minutos > 0) {
                    tempoLicenca = System.currentTimeMillis() + (minutos * 60 * 1000); // Converte minutos para milissegundos
                    salvarLicenca();
                    JOptionPane.showMessageDialog(licencaFrame, "Licença atualizada com sucesso!");
                    licencaFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(licencaFrame, "Digite um valor válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(licencaFrame, "Digite um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(lblMinutos);
        panel.add(txtMinutos);
        panel.add(btnAdicionar);

        licencaFrame.add(panel);
        licencaFrame.setVisible(true);
    }

    // Restante do código...

    // Tela de cadastro
    private static void abrirTelaCadastro() {
        JFrame cadastroFrame = new JFrame("Cadastro de Medicamentos");
        cadastroFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Impede o fechamento
        cadastroFrame.setResizable(false); // Impede o redimensionamento
        cadastroFrame.setUndecorated(true); // Remove a barra de título, impedindo a minimização
        cadastroFrame.setAlwaysOnTop(true); // Mantém a janela sempre no topo, incluindo sobre a barra de tarefas
        cadastroFrame.setSize(300, 400); // Tamanho fixo
        cadastroFrame.setLocation(0, Toolkit.getDefaultToolkit().getScreenSize().height - 400); // Posição fixa na extremidade esquerda inferior

        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\K-system21\\Sytem21\\imagem de fundo.png"); // Coloque o caminho da sua imagem aqui
                Image img = icon.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setOpaque(false); // Painel transparente
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2); // Reduzindo o espaçamento

        // Campo de Descrição
        JLabel lblDescricao = new JLabel("Descrição:");
        lblDescricao.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblDescricao.setForeground(Color.black);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblDescricao, gbc);

        JTextField txtDescricao = new JTextField(15); // Tamanho reduzido
        txtDescricao.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtDescricao, gbc);

        // Campo de Laboratório
        JLabel lblLaboratorio = new JLabel("Laboratório:");
        lblLaboratorio.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblLaboratorio.setForeground(Color.black);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblLaboratorio, gbc);

        JComboBox<String> comboLaboratorio = new JComboBox<>(laboratorios.toArray(new String[0]));
        comboLaboratorio.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comboLaboratorio.setBackground(new Color(50, 205, 50));
        comboLaboratorio.setForeground(Color.black);
        comboLaboratorio.setPreferredSize(new Dimension(150, 20)); // Tamanho reduzido
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(comboLaboratorio, gbc);

        JButton btnAddLaboratorio = new JButton("+");
        btnAddLaboratorio.setFont(new Font("SansSerif", Font.BOLD, 10)); // Fonte menor
        btnAddLaboratorio.setBackground(new Color(50, 205, 50));
        btnAddLaboratorio.setForeground(Color.black);
        btnAddLaboratorio.addActionListener(e -> adicionarItem("Laboratório", laboratorios, comboLaboratorio));
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(btnAddLaboratorio, gbc);

        // Campo de Para que serve
        JLabel lblParaQueServe = new JLabel("Para que serve:");
        lblParaQueServe.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblParaQueServe.setForeground(Color.black);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblParaQueServe, gbc);

        JComboBox<String> comboParaQueServe = new JComboBox<>(paraQueServe.toArray(new String[0]));
        comboParaQueServe.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comboParaQueServe.setBackground(new Color(50, 205, 50));
        comboParaQueServe.setForeground(Color.black);
        comboParaQueServe.setPreferredSize(new Dimension(150, 20)); // Tamanho reduzido
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(comboParaQueServe, gbc);

        JButton btnAddParaQueServe = new JButton("+");
        btnAddParaQueServe.setFont(new Font("SansSerif", Font.BOLD, 10)); // Fonte menor
        btnAddParaQueServe.setBackground(new Color(50, 205, 50));
        btnAddParaQueServe.setForeground(Color.black);
        btnAddParaQueServe.addActionListener(e -> adicionarItem("Para que serve", paraQueServe, comboParaQueServe));
        gbc.gridx = 2;
        gbc.gridy = 2;
        panel.add(btnAddParaQueServe, gbc);

        // Campo de Posologia
        JLabel lblPosologia = new JLabel("Posologia:");
        lblPosologia.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPosologia.setForeground(Color.black);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblPosologia, gbc);

        JComboBox<String> comboPosologia = new JComboBox<>(posologias.toArray(new String[0]));
        comboPosologia.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comboPosologia.setBackground(new Color(50, 205, 50));
        comboPosologia.setForeground(Color.black);
        comboPosologia.setPreferredSize(new Dimension(150, 20)); // Tamanho reduzido
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(comboPosologia, gbc);

        JButton btnAddPosologia = new JButton("+");
        btnAddPosologia.setFont(new Font("SansSerif", Font.BOLD, 10)); // Fonte menor
        btnAddPosologia.setBackground(new Color(50, 205, 50));
        btnAddPosologia.setForeground(Color.black);
        btnAddPosologia.addActionListener(e -> adicionarItem("Posologia", posologias, comboPosologia));
        gbc.gridx = 2;
        gbc.gridy = 3;
        panel.add(btnAddPosologia, gbc);

        // Campo de Princípio Ativo
        JLabel lblPrincipioAtivo = new JLabel("Princípio Ativo:");
        lblPrincipioAtivo.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPrincipioAtivo.setForeground(Color.black);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(lblPrincipioAtivo, gbc);

        JComboBox<String> comboPrincipioAtivo = new JComboBox<>(principiosAtivos.toArray(new String[0]));
        comboPrincipioAtivo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comboPrincipioAtivo.setBackground(new Color(50, 205, 50));
        comboPrincipioAtivo.setForeground(Color.black);
        comboPrincipioAtivo.setPreferredSize(new Dimension(150, 20)); // Tamanho reduzido
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(comboPrincipioAtivo, gbc);

        JButton btnAddPrincipioAtivo = new JButton("+");
        btnAddPrincipioAtivo.setFont(new Font("SansSerif", Font.BOLD, 10)); // Fonte menor
        btnAddPrincipioAtivo.setBackground(new Color(50, 205, 50));
        btnAddPrincipioAtivo.setForeground(Color.black);
        btnAddPrincipioAtivo.addActionListener(e -> adicionarItem("Princípio Ativo", principiosAtivos, comboPrincipioAtivo));
        gbc.gridx = 2;
        gbc.gridy = 4;
        panel.add(btnAddPrincipioAtivo, gbc);

        // Botão Salvar
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSalvar.setBackground(new Color(50, 205, 50));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.addActionListener(e -> {
            String descricao = txtDescricao.getText().trim();
            String laboratorio = (String) comboLaboratorio.getSelectedItem();
            String paraQue = (String) comboParaQueServe.getSelectedItem();
            String posologia = (String) comboPosologia.getSelectedItem();
            String principioAtivo = (String) comboPrincipioAtivo.getSelectedItem();

            if (descricao.isEmpty() || medicamentos.containsKey(descricao)) {
                JOptionPane.showMessageDialog(cadastroFrame, "Descrição inválida ou já cadastrada.", "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                medicamentos.put(descricao, new Medicamento(descricao, laboratorio, paraQue, posologia, principioAtivo));
                salvarDados();
                JOptionPane.showMessageDialog(cadastroFrame, "Medicamento salvo com sucesso!");
                cadastroFrame.dispose();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnSalvar, gbc);

        // Botão Voltar
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnVoltar.setBackground(new Color(50, 205, 50));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.addActionListener(e -> voltarAoMenuPrincipal(cadastroFrame));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnVoltar, gbc);

        cadastroFrame.add(panel);
        cadastroFrame.setVisible(true);
    }

    // Tela de busca
    private static void abrirTelaBusca() {
        JFrame buscaFrame = new JFrame("Buscar Produtos");
        buscaFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Impede o fechamento
        buscaFrame.setResizable(false); // Impede o redimensionamento
        buscaFrame.setUndecorated(true); // Remove a barra de título, impedindo a minimização
        buscaFrame.setAlwaysOnTop(true); // Mantém a janela sempre no topo, incluindo sobre a barra de tarefas
        buscaFrame.setSize(300, 400); // Tamanho fixo
        buscaFrame.setLocation(0, Toolkit.getDefaultToolkit().getScreenSize().height - 400); // Posição fixa na extremidade esquerda inferior

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\K-system21\\Sytem21\\imagem de fundo.png"); // Coloque o caminho da sua imagem aqui
                Image img = icon.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setOpaque(false);

        // Painel superior para os campos de busca
        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5)); // Layout de grade para organizar os componentes
        topPanel.setOpaque(false); // Painel transparente

        // Campo de texto para busca
        JTextField txtBusca = new JTextField();
        txtBusca.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Fonte menor
        topPanel.add(new JLabel("Digite o termo de busca:"));
        topPanel.add(txtBusca);

        // Combo box para selecionar o filtro
        JComboBox<String> comboFiltro = new JComboBox<>(new String[]{"Descrição", "Laboratório", "Para que serve", "Posologia", "Princípio Ativo"});
        comboFiltro.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Fonte menor
        comboFiltro.setBackground(new Color(50, 205, 50)); // Verde claro
        comboFiltro.setForeground(Color.black); // Texto branco
        topPanel.add(comboFiltro);

        // Combo box para selecionar o valor específico do filtro
        JComboBox<String> comboEspecifico = new JComboBox<>();
        comboEspecifico.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Fonte menor
        comboEspecifico.setBackground(new Color(50, 205, 50)); // Verde claro
        comboEspecifico.setForeground(Color.black); // Texto branco
        topPanel.add(comboEspecifico);

        // Botão de busca
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("SansSerif", Font.BOLD, 12)); // Fonte menor
        btnBuscar.setBackground(new Color(50, 205, 50)); // Verde claro
        btnBuscar.setForeground(Color.black); // Texto branco
        topPanel.add(btnBuscar);

        // Lista de resultados
        JList<String> listaResultados = new JList<>();
        listaResultados.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Fonte menor
        listaResultados.setOpaque(false); // Torna o JList transparente
        listaResultados.setForeground(Color.black); // Define a cor do texto como branco

        // Renderizador personalizado para a lista
        listaResultados.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setOpaque(false); // Torna o fundo de cada item transparente
                setForeground(Color.black); // Define a cor do texto como branco
                return this;
            }
        });

        // Scroll pane para a lista de resultados
        JScrollPane scrollPane = new JScrollPane(listaResultados);
        scrollPane.setOpaque(false); // Torna o JScrollPane transparente
        scrollPane.getViewport().setOpaque(false); // Torna o viewport transparente
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove a borda do JScrollPane

        // Adiciona os componentes ao painel principal
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Ação para atualizar o combo box específico com base no filtro selecionado
        comboFiltro.addActionListener(e -> {
            String filtroSelecionado = (String) comboFiltro.getSelectedItem();
            comboEspecifico.removeAllItems();
            switch (filtroSelecionado) {
                case "Laboratório":
                    laboratorios.forEach(comboEspecifico::addItem);
                    break;
                case "Para que serve":
                    paraQueServe.forEach(comboEspecifico::addItem);
                    break;
                case "Posologia":
                    posologias.forEach(comboEspecifico::addItem);
                    break;
                case "Princípio Ativo":
                    principiosAtivos.forEach(comboEspecifico::addItem);
                    break;
            }
        });

        // Ação para realizar a busca
        btnBuscar.addActionListener(e -> {
            String termoBusca = txtBusca.getText().trim().toLowerCase();
            String filtro = (String) comboFiltro.getSelectedItem();
            String especificoSelecionado = (String) comboEspecifico.getSelectedItem();

            List<String> resultados = medicamentos.values().stream()
                    .filter(med -> {
                        switch (filtro) {
                            case "Descrição":
                                return med.descricao.toLowerCase().contains(termoBusca);
                            case "Laboratório":
                                return med.laboratorio.equals(especificoSelecionado);
                            case "Para que serve":
                                return med.paraQueServe.equals(especificoSelecionado);
                            case "Posologia":
                                return med.posologia.equals(especificoSelecionado);
                            case "Princípio Ativo":
                                return med.principioAtivo.equals(especificoSelecionado);
                            default:
                                return false;
                        }
                    })
                    .map(med -> med.descricao)
                    .sorted()
                    .collect(Collectors.toList());

            listaResultados.setListData(resultados.toArray(new String[0]));
        });

        // Ação para exibir os detalhes do medicamento selecionado
        listaResultados.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selecionado = listaResultados.getSelectedValue();
                if (selecionado != null) {
                    Medicamento med = medicamentos.get(selecionado);
                    JOptionPane.showMessageDialog(buscaFrame,
                            "Descrição: " + med.descricao + "\n" +
                                    "Para que serve: " + med.paraQueServe + "\n" +
                                    "Posologia: " + med.posologia + "\n" +
                                    "Princípio Ativo: " + med.principioAtivo,
                            "Laboratório: " + med.laboratorio + "\n" +
                                    "Detalhes do Produto", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Botão Voltar
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnVoltar.setBackground(new Color(50, 205, 50));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.addActionListener(e -> voltarAoMenuPrincipal(buscaFrame));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnVoltar);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        buscaFrame.add(panel);
        buscaFrame.setVisible(true);
    }

    // Método para adicionar itens às listas
    private static void adicionarItem(String tipo, Set<String> lista, JComboBox<String> comboBox) {
        String novoItem = JOptionPane.showInputDialog("Adicionar " + tipo + ":");
        if (novoItem != null && !novoItem.trim().isEmpty() && !lista.contains(novoItem.trim())) {
            lista.add(novoItem.trim());
            comboBox.addItem(novoItem.trim());
            salvarDados();
        }
    }

    // Persistência de dados
    private static void salvarDados() {
        salvarSet(laboratorios, "laboratorios.txt");
        salvarSet(principiosAtivos, "principiosAtivos.txt");
        salvarSet(paraQueServe, "paraQueServe.txt");
        salvarSet(posologias, "posologias.txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter("medicamentos.txt"))) {
            for (Medicamento med : medicamentos.values()) {
                writer.println(med.descricao + ";" + med.laboratorio + ";" + med.paraQueServe + ";" + med.posologia + ";" + med.principioAtivo);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar dados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void carregarDados() {
        laboratorios = carregarSet("laboratorios.txt");
        principiosAtivos = carregarSet("principiosAtivos.txt");
        paraQueServe = carregarSet("paraQueServe.txt");
        posologias = carregarSet("posologias.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader("medicamentos.txt"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length == 5) {
                    medicamentos.put(dados[0], new Medicamento(dados[0], dados[1], dados[2], dados[3], dados[4]));
                }
            }
        } catch (IOException ignored) {
        }
    }

    private static Set<String> carregarSet(String arquivo) {
        Set<String> set = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                set.add(linha);
            }
        } catch (IOException ignored) {
        }
        return set;
    }

    private static void salvarSet(Set<String> set, String arquivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo))) {
            for (String item : set) {
                writer.println(item);
            }
        } catch (IOException ignored) {
        }
    }

    // Método para voltar ao menu principal
    private static void voltarAoMenuPrincipal(JFrame frameAtual) {
        frameAtual.dispose(); // Fecha a janela atual
        criarMenuPrincipal(); // Reabre o menu principal
    }
}