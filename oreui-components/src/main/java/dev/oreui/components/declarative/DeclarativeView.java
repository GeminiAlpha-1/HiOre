package dev.oreui.components.declarative;

import dev.oreui.components.ComponentContext;
import dev.oreui.components.ControlSize;
import dev.oreui.components.LabelOverflow;
import dev.oreui.components.LiteralText;
import dev.oreui.components.OreButton;
import dev.oreui.components.OreLabel;
import dev.oreui.components.OreSurface;
import dev.oreui.components.OreWidget;
import dev.oreui.components.SurfaceVariant;
import dev.oreui.core.geom.Insets;
import dev.oreui.layout.LayoutStyle;
import dev.oreui.theme.ButtonVariant;
import dev.oreui.theme.TypeRole;

import java.util.Map;

public final class DeclarativeView {
    private static final float DEFAULT_TEXT_WIDTH = 160f;

    private DeclarativeView() {
    }

    public static OreWidget build(String html, String css, ComponentContext context) {
        HtmlNode document = HtmlParser.parse(html);
        CssSheet sheet = CssSheet.parse(css);
        if (document.children().isEmpty()) {
            throw new IllegalArgumentException("html must contain a root element");
        }
        if (document.children().size() == 1) {
            return buildNode(document.children().get(0), sheet, context);
        }
        OreSurface root = new OreSurface(context, SurfaceVariant.PANEL);
        for (HtmlNode child : document.children()) {
            root.addChild(buildNode(child, sheet, context));
        }
        return root;
    }

    private static OreWidget buildNode(HtmlNode node, CssSheet sheet, ComponentContext context) {
        Map<String, Float> style = sheet.styleFor(node);
        float width = style.getOrDefault("width", 0f);
        float height = style.getOrDefault("height", 0f);
        OreWidget widget = switch (node.tag()) {
            case "panel" -> new OreSurface(context, SurfaceVariant.PANEL);
            case "card", "surface" -> new OreSurface(context, SurfaceVariant.CARD);
            case "row", "column" -> container(node, context);
            case "label", "text" -> new OreLabel(context,
                    new LiteralText(node.text().trim()), TypeRole.CAPTION,
                    LabelOverflow.CLIP, (int) (width > 0f ? width : DEFAULT_TEXT_WIDTH));
            case "button", "action" -> new OreButton(context, ButtonVariant.PRIMARY, ControlSize.MD,
                    new LiteralText(node.text().trim().isEmpty() ? "OK" : node.text().trim()), () -> {
                    });
            default -> container(node, context);
        };
        applyStyle(widget, style);
        if (widget instanceof OreSurface surface) {
            for (HtmlNode child : node.children()) {
                surface.addChild(buildNode(child, sheet, context));
            }
        }
        return widget;
    }

    private static OreSurface container(HtmlNode node, ComponentContext context) {
        OreSurface surface = new OreSurface(context, SurfaceVariant.CARD);
        surface.style().direction(node.tag().equals("row")
                ? LayoutStyle.Direction.ROW : LayoutStyle.Direction.COLUMN);
        return surface;
    }

    private static void applyStyle(OreWidget widget, Map<String, Float> style) {
        Float width = style.get("width");
        Float height = style.get("height");
        Float gap = style.get("gap");
        Float padding = style.get("padding");
        if (width != null) {
            widget.style().width(width);
        }
        if (height != null) {
            widget.style().height(height);
        }
        if (gap != null) {
            widget.style().gap(gap);
        }
        if (padding != null) {
            widget.style().padding(new Insets(padding, padding, padding, padding));
        }
    }
}