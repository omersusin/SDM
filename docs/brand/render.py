#!/usr/bin/env python3
"""Grabbit logo v2 (brandkit: product-action mark, geometry, no boring letter)."""
import os
from PIL import Image, ImageDraw

OUT = "/data/data/com.termux/files/usr/tmp/opencode/logo2"
os.makedirs(OUT, exist_ok=True)

BG = (13, 43, 34, 255)       # deep green
FG = (255, 255, 255, 255)    # white
LIME = (198, 241, 53, 255)   # accent


def rounded_rect(d, box, r, fill):
    d.rounded_rectangle(box, radius=r, fill=fill)


def draw_mark(d, s, color, accent):
    cx = s / 2
    # shaft
    sw = s * 0.11
    top = s * 0.20
    mid = s * 0.60
    rounded_rect(d, [cx - sw / 2, top, cx + sw / 2, mid], r=sw / 2, fill=color)
    # arrow head
    hw = s * 0.20
    d.polygon(
        [(cx - hw, mid - s * 0.02), (cx + hw, mid - s * 0.02), (cx, mid + s * 0.13)],
        fill=color,
    )
    # tray
    ty = s * 0.72
    th = s * 0.075
    tw = s * 0.42
    rounded_rect(d, [cx - tw / 2, ty, cx + tw / 2, ty + th], r=th / 2, fill=accent)


def full_bleed(s, bg, glyph=True, mono=False):
    img = Image.new("RGBA", (s, s), bg)
    if glyph:
        d = ImageDraw.Draw(img)
        if mono:
            draw_mark(d, s, FG, FG)
        else:
            draw_mark(d, s, FG, LIME)
    return img


def adaptive_fg(s, mono=False):
    img = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    inner = int(s * 0.667)
    off = (s - inner) // 2
    layer = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    if mono:
        draw_mark(d, inner, FG, FG)
    else:
        draw_mark(d, inner, FG, LIME)
    img.alpha_composite(layer, (off, off))
    return img


S = 1024
full_bleed(S, BG).save(f"{OUT}/icon-1024.png")
adaptive_fg(S).save(f"{OUT}/adaptive-fg-1024.png")
Image.new("RGBA", (S, S), BG).save(f"{OUT}/adaptive-bg-1024.png")
mono = Image.new("RGBA", (S, S), (0, 0, 0, 0))
draw_mark(ImageDraw.Draw(mono), int(S * 0.667), FG, FG)
mono2 = Image.new("RGBA", (S, S), (0, 0, 0, 0))
d = ImageDraw.Draw(Image.new("RGBA", (S, S), (0, 0, 0, 0)))
# center mono
tmp = Image.new("RGBA", (S, S), (0, 0, 0, 0))
draw_mark(ImageDraw.Draw(tmp), int(S * 0.667), FG, FG)
off = (S - int(S * 0.667)) // 2
mono.alpha_composite(tmp, (off, off))
mono.save(f"{OUT}/monochrome-1024.png")
for name, px in {"xxxhdpi": 192, "xxhdpi": 144, "xhdpi": 96, "hdpi": 72, "mdpi": 48}.items():
    full_bleed(px, BG).save(f"{OUT}/mipmap-{name}.png")
print("done", OUT)
