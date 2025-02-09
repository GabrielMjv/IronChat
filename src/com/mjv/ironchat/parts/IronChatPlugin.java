package com.mjv.ironchat.parts;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Composite;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;

public class IronChatPlugin {

    // Área de histórico para exibir as mensagens
    private Text chatHistory;
    // Campo de entrada para digitar a mensagem
    private Text chatInput;

    // Altura máxima em número de linhas para o campo de entrada
    private static final int MAX_LINES = 15;
    // Altura de uma linha (calculada uma única vez)
    private int lineHeight;

    @PostConstruct
    public void createPartControl(Composite parent) {
        // Layout principal com 1 coluna, dividindo a área de histórico (topo)
        // e o campo de entrada (base)
        GridLayout mainLayout = new GridLayout(1, false);
        mainLayout.marginWidth = 10;
        mainLayout.marginHeight = 10;
        mainLayout.verticalSpacing = 10;
        parent.setLayout(mainLayout);

        // Área de histórico (somente leitura) com scroll automático
        chatHistory = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
        chatHistory.setText("Histórico de mensagens...\n(implementação futura)");
        GridData historyData = new GridData(SWT.FILL, SWT.FILL, true, true);
        chatHistory.setLayoutData(historyData);

        // Campo de entrada para digitar mensagem
        // SWT.MULTI e SWT.WRAP para permitir o redimensionamento e a quebra automática
        // SWT.V_SCROLL para que, após o limite de linhas, apareça a barra de rolagem
        chatInput = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData inputData = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        // Inicialmente, vamos definir a altura para 1 linha
        inputData.heightHint = 75;
        chatInput.setLayoutData(inputData);

        // Calcular a altura de uma linha usando o GC (Graphics Context)
        GC gc = new GC(chatInput);
        lineHeight = gc.getFontMetrics().getHeight();
        gc.dispose();

        // Listener para ajustar dinamicamente a altura do campo de entrada,
        // limitado a MAX_LINES. Conforme o usuário digita, o widget cresce para cima,
        // reduzindo a área de histórico.
        chatInput.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                // Obter o número atual de linhas
                int lines = chatInput.getLineCount();
                // Determinar quantas linhas serão exibidas (máximo MAX_LINES)
                int desiredLines = Math.min(lines, MAX_LINES);
                // Calcular a nova altura; aqui adicionamos 10 pixels de margem interna
                int newHeight = desiredLines * lineHeight + 10;
                GridData gd = (GridData) chatInput.getLayoutData();
                if (gd.heightHint != newHeight) {
                    gd.heightHint = newHeight;
                    // Recalcula o layout do parent para que o histórico seja ajustado
                    chatInput.getParent().layout();
                }
            }
        });

        // Listener para detectar o pressionamento de teclas.
        // Se o usuário pressionar apenas Enter, a mensagem é submetida.
        // Se pressionar Shift+Enter, insere-se uma quebra de linha.
        chatInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Verifica se a tecla pressionada é Enter (CR ou Keypad CR)
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    // Se o Shift não estiver pressionado, submete a mensagem
                    if ((e.stateMask & SWT.SHIFT) == 0) {
                        String text = chatInput.getText().trim();
                        if (!text.isEmpty()) {
                            sendText(text);
                            // Após enviar, limpa o campo
                            chatInput.setText("");
                        }
                        // Impede a inserção de uma nova linha
                        e.doit = false;
                    }
                    // Se Shift estiver pressionado, permite a quebra de linha (comportamento padrão)
                }
            }
        });
    }

    /**
     * Simula o envio do texto digitado.
     * Aqui você pode implementar a lógica para adicionar a mensagem ao histórico,
     * enviar para um servidor, etc.
     */
    private void sendText(String text) {
        System.out.println("Texto enviado: " + text);
        // Exemplo de atualização do histórico: adiciona a nova mensagem ao conteúdo atual
        chatHistory.append(text + System.lineSeparator());
      
        // Opcional: rolar automaticamente o histórico para o final
        chatHistory.setSelection(chatHistory.getCharCount());
    }

    @Focus
    public void setFocus() {
        chatInput.setFocus();
    }

    @Inject
    @Optional
    public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object o) {
        // Exemplo de injeção opcional; ajuste conforme sua necessidade
        if (chatHistory != null && o != null) {
            chatHistory.setText("Current single selection class is : " + o.getClass().getSimpleName());
        }
    }

    @Inject
    @Optional
    public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object[] selectedObjects) {
        if (chatHistory != null && selectedObjects != null) {
            chatHistory.setText("Multiple selection of " + selectedObjects.length + " objects");
        }
    }
}
