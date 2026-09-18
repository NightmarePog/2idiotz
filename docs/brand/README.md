# Think different Academy — pravidla vizuální identity

Veškeré nové a upravované UI musí dodržovat tento manuál. Platí pro stránky, komponenty, formuláře, stavy načítání/chyb, responzivní varianty i další vizuální výstupy aplikace.

## Zdroje

- [Sdílená složka značky](https://drive.google.com/drive/folders/1_YiVHEABBMP7sALb5oukPVZqxszg9Z2U)
- [Originální PDF](https://drive.google.com/file/d/1aqmct6ZsDR3G-q7WE-wVxSPxTBCZDOX2/view): `TdA27_Think-different-Academy_BRANDmanual.pdf`
- [Lokální kopie manuálu](tda-brand-manual.pdf), načtená 17. 9. 2026. Při změně oficiálního manuálu aktualizujte kopii a tento souhrn.
- [Fonty](https://drive.google.com/drive/folders/1fcYMnhniXRS_YUkWfRTmDPbsWzQxgV2b)
- [Ikony](https://drive.google.com/drive/folders/1qcL8eSoSL9xE-zfG1jicAPJ4FB1yfDs6)
- [Loga](https://drive.google.com/drive/folders/16w2QvMw0F5DbTCSBBDc_5pOz72ckQfu_)

## Pravidla přímo z manuálu

### Název a charakter

Název značky je **Think different Academy**. Identita podporuje kritické a kreativní myšlení a moderní, interaktivní vzdělávání (strana 2).

### Barvy

Zelená reprezentuje rozvoj, modrá vzdělávání; obě mají být ve značce patrné (strana 7).

| Barva | HEX | Role podle manuálu |
| --- | --- | --- |
| Zelená | `#91F5AD` | Základní |
| Modrá | `#0070BB` | Základní |
| Tmavší modrá | `#0257A5` | Doplňková |
| Bílá | `#FFFFFF` | Doplňková |
| Téměř černá | `#1A1A1A` | Doplňková |
| Zelenomodrá | `#6DD4B1` | Doplňková |
| Tyrkysová | `#49B3B4` | Doplňková |
| Modrozelená | `#2592B8` | Doplňková |

Kompletní paleta je na straně 8. Manuál neurčuje přiřazení barev ke konkrétním stavům tlačítek nebo formulářů.

### Typografie

Používejte **Dosis**, hravé zaoblené bezpatkové písmo (strana 9). Manuál neurčuje konkrétní velikosti, řezy ani řádkování pro webové komponenty.

### Logo a ikony

- Používejte dodané varianty loga: erb se žárovkou / maskotem Táda a názvem značky (strany 3–4).
- Dodržujte poměr stran; logo nedeformujte (strana 5).
- Pro tmavá pozadí použijte odpovídající dodanou variantu (strana 4).
- Zachovejte ochrannou zónu podle diagramů pro jednotlivé varianty na straně 6. Jednotky `1/3` vztahujte k vyznačené části loga, nikoli automaticky k celé šířce sestavy.
- Maskota Tádu lze používat jako ikonu (strana 10). Upřednostněte oficiální podklady před překreslováním.

## Projektové zásady pro implementaci

Následující zásady doplňují manuál; nejsou jeho citací:

- Zachovejte Svelte, Tailwind a shadcn-svelte. Sdílenou typografii a barvy definujte centrálně v `frontend/src/app.css` a používejte sémantické tokeny komponent.
- Při první úpravě stávajícího UI nahraďte výchozí Inter a neutrální shadcn téma značkovou typografií a paletou. Samotná přítomnost shadcn nezajišťuje soulad se značkou.
- Rozestupy, velikosti písma, zaoblení a komponentní stavy, které manuál nespecifikuje, řešte konzistentně sdílenými Tailwind tokeny a existujícími komponentami. Nevydávejte vlastní hodnoty za požadavky manuálu.
- Ověřujte čitelnost kontrastních dvojic, viditelný fokus, popisky a responzivní chování; nepoužívejte barvu jako jediný nositel informace.
- U každé UI změny kontrolujte kromě funkce také soulad se značkou a návaznost na ostatní části aplikace.

## Lokální webové podklady

- `backend/src/main/resources/assets/brand/tda-logo.svg`: oficiální horizontální varianta `Think-different-Academy_LOGO_oficialni_1.svg` ze [sdílené složky SVG](https://drive.google.com/drive/folders/1HUzlC_QSbWWVgJNwh-i6-O_1Lp-KQQQ1), soubor ID `1ZgNgsFD-xGU2rCMUZWipLJnE1HUc3OAz`. Staženo 17. 9. 2026; poměr stran zachován.
- `backend/src/main/resources/assets/brand/tda-logo-dark.svg`: nezměněná oficiální varianta pro tmavé pozadí, [Think-different-Academy_LOGO_oficialni_1_dark-mode.svg](https://drive.google.com/file/d/1OOddazgOInkkhpwNiUHqu_EwT3lTvE2l/view), stažená 18. 9. 2026 ze stejné složky SVG. Modrý erb a bílé písmo na průhledném pozadí. Hlavička přepíná obě varianty podle tématu bez podkladové dlaždice.
- `backend/src/main/resources/assets/fonts/dosis-variable.ttf`: proměnlivé písmo Dosis z [Google Fonts](https://github.com/google/fonts/tree/main/ofl/dosis), lokálně hostované včetně licence `OFL-Dosis.txt`.
- Světlé podklady, tlumené texty a barvy chyb v CSS doplňují oficiální paletu pro čitelnost UI; nejde o další barvy předepsané manuálem.

## Texty v rozhraní

Text omezujte na nezbytné názvy, popisky ovládání, údaje a stručnou zpětnou vazbu. Nepřidávejte dekorativní slogany, opakované označení aplikace, duplicitní popisky fotek ani vysvětlování zřejmých ovládacích prvků. Zachovejte přístupné názvy a popisky formulářů.
