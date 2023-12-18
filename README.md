![AnhyShop Logo](https://dev.anh.ink/images/shoplogo.png "AnhyShop Minecraft Plugin. Anhydrite Developer")

# AnhyShop
Is a unique plugin for creating merchants and trading on Minecraft servers, based on the trading inventory of villagers. Tested on `Spigot`, `Paper`, `Purpur` server cores, version `1.20.2`.

### Essential Dependencies for [**AnhyShop**](https://dev.anh.ink/anhyshop/)
The primary requirement for **AnhyShop** to function is the installation of the [**AnhyLibAPI**](https://dev.anh.ink/anhylibapi/) library. Without **AnhyLibAPI**, **AnhyShop** will not operate. It provides translation of system messages and multilingual names for merchants.

Additionally, **AnhyShop** is compatible with [**AnhyLingo**](https://dev.anh.ink/anhylingo/) version 0.3.3 or higher. Installing **AnhyLingo** expands the multilingual capabilities of **AnhyShop**. However, its absence does not affect the primary operation of **AnhyShop**.

## Functionality of the Plugin
**AnhyShop: Intuitive Plugin for Creating Administrative Stores in Minecraft**

- **Simplicity and Convenience**: No complicated configurations, all data is automatically stored in the embedded SQLite database (file `shops.db` in the plugin folder).
- **Data Security**: Information about traders is encrypted.
- **Flexible Use**: Open trade via console commands or player commands with appropriate permissions.

### Administrator Commands
(with permission `anhyshop.*`)

#### Reloading Commands
- **Reloading the plugin** `(anhyshop.reload)`: `/shop reload` - Reloads the language files, configurations, and traders from the database.

### Work with Traders
(with permission `anhyshop.trader.*`)

1. **Information about traders** `(anhyshop.trader.view)`: `/shop list` - Displays a list of all traders.
2. **Create a new trader** `(anhyshop.trader.create)`: `/shop newt <name>` - Creates a new trader.
3. **Remove trader** `(anhyshop.trader.delete)`: `/shop delt <key>` - Deletes a trader.
4. **Rename trader** `(anhyshop.trader.rename)`: `/shop rename <key> <new_name>` - Renames a trader.
5. **Open trade inventory** `(anhyshop.trader.open)`: `/shop open <key> <player_name>` - Opens trade for a player.
6. **Trade by yourself** `(anhyshop.trader.trade)`: `/shop trade <key>` - Opens a trade.

### Operations with Goods
(with permission `anhyshop.product.*`)

1. **Add a new trade** `(anhyshop.product.add)`: `/shop add <key>` - To add a trade, place items in slots 1, 2, 3 of your inventory.
2. **Replace an existing trade** `(anhyshop.product.replace)`: `/shop replace <key>` - Replace or add products.
3. **Delete an existing trade** `(anhyshop.product.remove)`: `/shop remove <key>` - Remove a product from bidding.

![Add Trade](/images/addtrade.png "Work with traders")

### Directory Structure
- `lang`: Files with translations of plugin system messages and translations of trader names.

### Language Personalization
**Multilingual Functionality in AnhyShop with AnhyLibAPI and AnhyLingo Integration**

1. **System Messages Localization** (AnhyLibAPI): Stored in `shop_XX.yml` files within the `lang` folder.
2. **Trader Names Translation** (AnhyLibAPI): Add translations in `traders_XX.yml`.
3. **Multilingual Item Display in Trades** (AnhyLingo): Trades involving multilingual items display these items in the player's chosen language.
