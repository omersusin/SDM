#!/usr/bin/env python3
"""Grabbit logo renderer (icon-generation skill adapted: Pillow, no Chrome on device)."""
import os
from PIL import Image, ImageDraw, ImageFont

OUT = "/data/data/com.termux/files/usr/tmp/opencode/logo"
os.makedirs(OUT, exist_ok=True)

BG = (13, 59, 46, 255)      # deep green
FG = (255, 255, 255, 255)   # white
BOLD = "/system/fonts/DroidSans-Bold.ttf"


def draw_glyph(draw, s, cx, cy, color):
    # Big G
    font = ImageFont.truetype(BOLD, int(s * 0.52))
    g = "G"
    bb = draw.textbbox((0, 0), g, font=font)
    w, h = bb[2] - bb[0], bb[3] - bb[1]
    draw.text((cx - w / 2 - bb[0], cy - h / 2 - bb[1] - s * 0.03), g, font=font, fill=color)
    # Down arrow tucked right of the G's bar
    ax, ay, aw = cx + s * 0.22, cy + s * 0.10, s * 0.06
    ah = s * 0.17
    draw.rectangle([ax - aw * 0.28, ay - ah / 2, ax + aw * 0.28, ay + ah * 0.18], fill=color)
    draw.polygon(
        [(ax - aw, ay + ah * 0.05), (ax + aw, ay + ah * 0.05), (ax, ay + ah / 2)],
        fill=color,
    )


def full_bleed(s, bg, glyph_color, glyph=True):
    img = Image.new("RGBA", (s, s), bg)
    if glyph:
        draw_glyph(ImageDraw.Draw(img), s, s / 2, s / 2, glyph_color)
    return img


def adaptive_fg(s):
    # 72dp glyph in 108dp viewport => scale 0.667, centered
    img = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    inner = int(s * 0.667)
    off = (s - inner) // 2
    layer = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    draw_glyph(ImageDraw.Draw(layer), inner, inner / 2, inner / 2, FG)
    img.alpha_composite(layer, (off, off))
    return img


def adaptive_bg(s):
    return Image.new("RGBA", (s, s), BG)


S = 1024
full_bleed(S, BG, FG).save(f"{OUT}/icon-1024.png")
adaptive_fg(S).save(f"{OUT}/adaptive-fg-1024.png")
adaptive_bg(S).save(f"{OUT}/adaptive-bg-1024.png")
# monochrome: white glyph on transparent (themed icons tint it)
mono = Image.new("RGBA", (S, S), (0, 0, 0, 0))
d = ImageDraw.Draw(mono)
draw_glyph(d, int(S * 0.667), S / 2, S / 2, FG)
mono.save(f"{OUT}/monochrome-1024.png")
# legacy mipmaps (foreground composited over bg)
DPI = {"xxxhdpi": 192, "xxhdpi": 144, "xhdpi": 96, "hdpi": 72, "mdpi": 48}
for name, px in DPI.items():
    full_bleed(px, BG, FG).save(f"{OUT}/mipmap-{name}.png")
print("done", OUT)
