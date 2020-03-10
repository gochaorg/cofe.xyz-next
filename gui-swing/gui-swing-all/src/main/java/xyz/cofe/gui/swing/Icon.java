/*
 * The MIT License
 *
 * Copyright 2014 Kamnev Georgiy (nt.gocha@gmail.com).
 *
 * Данная лицензия разрешает, безвозмездно, лицам, получившим копию данного программного
 * обеспечения и сопутствующей документации (в дальнейшем именуемыми "Программное Обеспечение"),
 * использовать Программное Обеспечение без ограничений, включая неограниченное право на
 * использование, копирование, изменение, объединение, публикацию, распространение, сублицензирование
 * и/или продажу копий Программного Обеспечения, также как и лицам, которым предоставляется
 * данное Программное Обеспечение, при соблюдении следующих условий:
 *
 * Вышеупомянутый копирайт и данные условия должны быть включены во все копии
 * или значимые части данного Программного Обеспечения.
 *
 * ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ ПРЕДОСТАВЛЯЕТСЯ «КАК ЕСТЬ», БЕЗ ЛЮБОГО ВИДА ГАРАНТИЙ,
 * ЯВНО ВЫРАЖЕННЫХ ИЛИ ПОДРАЗУМЕВАЕМЫХ, ВКЛЮЧАЯ, НО НЕ ОГРАНИЧИВАЯСЬ ГАРАНТИЯМИ ТОВАРНОЙ ПРИГОДНОСТИ,
 * СООТВЕТСТВИЯ ПО ЕГО КОНКРЕТНОМУ НАЗНАЧЕНИЮ И НЕНАРУШЕНИЯ ПРАВ. НИ В КАКОМ СЛУЧАЕ АВТОРЫ
 * ИЛИ ПРАВООБЛАДАТЕЛИ НЕ НЕСУТ ОТВЕТСТВЕННОСТИ ПО ИСКАМ О ВОЗМЕЩЕНИИ УЩЕРБА, УБЫТКОВ
 * ИЛИ ДРУГИХ ТРЕБОВАНИЙ ПО ДЕЙСТВУЮЩИМ КОНТРАКТАМ, ДЕЛИКТАМ ИЛИ ИНОМУ, ВОЗНИКШИМ ИЗ, ИМЕЮЩИМ
 * ПРИЧИНОЙ ИЛИ СВЯЗАННЫМ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ ИЛИ ИСПОЛЬЗОВАНИЕМ ПРОГРАММНОГО ОБЕСПЕЧЕНИЯ
 * ИЛИ ИНЫМИ ДЕЙСТВИЯМИ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ.
 */
package xyz.cofe.gui.swing;

//import xyz.cofe.files.ResourceObject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Иконка
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class Icon implements javax.swing.Icon, Serializable
{
    protected javax.swing.Icon delegateIcon = null;
    protected Object iconSource = null;

    /**
     * Конструктор
     * @param urlIcon ссылка на иконку
     */
    public Icon(URL urlIcon)
    {
        if (urlIcon == null) {
            throw new IllegalArgumentException("urlIcon == null");
        }
        this.iconSource = urlIcon;
        delegateIcon = new ImageIcon(urlIcon);
    }

    /**
     * Констуктор
     * @param fileIcon файл с иконкой
     */
    public Icon(File fileIcon)
    {
        if (fileIcon == null) {
            throw new IllegalArgumentException("fileIcon == null");
        }
        this.iconSource = fileIcon;
		if( !fileIcon.exists() || !fileIcon.canRead() || !fileIcon.isFile() ){
			delegateIcon = new ImageIcon(getEmptyImage());
		}else{
			delegateIcon = new ImageIcon(fileIcon.getAbsolutePath());
		}
    }

//    /**
//     * Конструктор
//     * @param resource ресурс
//     */
//    public Icon(ResourceObject resource)
//    {
//        if (resource == null) {
//            throw new IllegalArgumentException("resource == null");
//        }
//        this.iconSource = resource;
//		if( resource.getResourceURL()==null ){
//			delegateIcon = new ImageIcon(getEmptyImage());
//		}else{
//			delegateIcon = new ImageIcon(resource.getResourceURL());
//		}
//    }

    /**
     * Указывает источник иконки. <br>
     * Может быть один из параметров конструктора.
     * @return Источник иконки
     */
    public Object getIconSource()
    {
        return iconSource;
    }

    /**
     * Отображает иконку
     * @param c Компонент
     * @param g Контекст
     * @param x Координаты
     * @param y Координаты
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        delegateIcon.paintIcon(c, g, x, y);
    }

    /**
     * Возвращает ширину иконки
     * @return Ширина
     */
    @Override
    public int getIconWidth()
    {
        return delegateIcon.getIconWidth();
    }

    /**
     * Возвращает высоту иконки
     * @return Иконка
     */
    @Override
    public int getIconHeight()
    {
        return delegateIcon.getIconHeight();
    }

	protected BufferedImage createEmtpry(int tw,int th){
		int w = 100;
		int h = 100;
		int strokeW = 3;
		int indent = 3;
		String txt = "?";

		BufferedImage im = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D gs = (Graphics2D) im.getGraphics();

		Stroke stroke = new BasicStroke(strokeW);

		gs.setPaint(Color.white);
		gs.setStroke(stroke);
		gs.fillRect(0, 0, w, h);

		Font fnt = new Font(Font.SANS_SERIF, Font.BOLD, 10);
		FontMetrics fmtr = gs.getFontMetrics(fnt);

		gs.setColor(Color.black);
		gs.setFont(fnt);
		gs.drawString(txt, indent, fmtr.getHeight()+indent);
		Rectangle2D rect = fmtr.getStringBounds(txt, gs);

		gs.setColor(Color.red);
		gs.drawRect(0, 0, (int)rect.getWidth()+indent*2, (int)rect.getHeight()+indent*2);

		BufferedImage res = new BufferedImage(tw, th, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D gs2 = (Graphics2D)res.getGraphics();
		gs2.drawImage(im, 0, 0, tw-1, th-1, 0, 0, (int)rect.getWidth()+indent*2, (int)rect.getHeight()+indent*2, null);

		return res;
	}

	protected static BufferedImage emptyImage = null;

	protected BufferedImage getEmptyImage(){
		if( emptyImage!=null )return emptyImage;
		emptyImage = createEmtpry(16, 16);
		return emptyImage;
	}
}
