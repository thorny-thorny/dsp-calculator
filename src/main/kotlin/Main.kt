import kotlin.math.ceil

interface ProductionItem {
  val name: String
}

data class ProductionItemsBundle(val productionItem: ProductionItem, val amount: Float)

data class Resource(override val name: String): ProductionItem

data class Recipe(override val name: String, val itemsPerRecipe: Int, val recipesPerMinute: Float, val requiredBundles: List<ProductionItemsBundle>): ProductionItem {
  constructor(name: String, itemsPerRecipe: Int, recipesPerMinute: Float, requiredBundle: ProductionItemsBundle): this(name, itemsPerRecipe, recipesPerMinute, listOf(requiredBundle))
}

operator fun Int.times(resource: Resource) = ProductionItemsBundle(resource, this.toFloat())

operator fun Int.times(item: Recipe) = ProductionItemsBundle(item, this.toFloat())

operator fun ProductionItemsBundle.plus(other: ProductionItemsBundle): List<ProductionItemsBundle> = listOf(this, other)

data class SummaryItem(val item: ProductionItem, var amount: Float)

fun calculateProduction(recipe: Recipe, amount: Float) {
  val summaryItems = mutableListOf<SummaryItem>()

  fun calculateRecipe(recipe: Recipe, amount: Float) {
    recipe.requiredBundles.forEach { bundle ->
      val amountToAdd = bundle.amount * amount / recipe.itemsPerRecipe
      val summaryItem = summaryItems.firstOrNull { it.item == bundle.productionItem }
      if (summaryItem == null) {
        summaryItems.add(SummaryItem(bundle.productionItem, amountToAdd))
      } else {
        summaryItem.amount += amountToAdd
      }

      if (bundle.productionItem is Recipe) {
        calculateRecipe(bundle.productionItem, amountToAdd)
      }
    }
  }

  calculateRecipe(recipe, amount)

  println("For $amount of ${recipe.name} you need:")
  summaryItems.forEach {
    val factories = when (it.item is Recipe) {
      true -> "${ceil(it.amount / (it.item.itemsPerRecipe * it.item.recipesPerMinute)).toInt()} factories"
      else -> '-'
    }
    println("${it.item.name} - ${it.amount} items / $factories")
  }
}

fun main() {
  val ironOre = Resource("Iron ore")
  val copperOre = Resource("Coppper ore")
  val coal = Resource("Coal")
  val siliconOre = Resource("Silicon ore")
  val stone = Resource("Stone")
  val crudeOil = Resource("Crude oil")
  val water = Resource("Water")
  val titaniumOre = Resource("Titanium ore")
  val hydrogen = Resource("Hydrogen")

  val ironIngot = Recipe("Iron ingot", 1, 60f, 1 * ironOre)
  val copperIngot = Recipe("Copper ingot", 1, 60f, 1 * copperOre)
  val energeticGraphite = Recipe("Energetic graphite", 1, 30f, 2 * coal)
  val siliconIngot = Recipe("Silicon ingot", 1, 30f, 2 * siliconOre)
  val glass = Recipe("Glass", 1, 30f, 2 * stone)
  val refinedOil = Recipe("Refined oil", 2, 15f, 2 * crudeOil)
  val titaniumIngot = Recipe("Titanium ingot", 1, 30f, 2 * titaniumOre)
  val deuterium = Recipe("Deuterium", 5, 24f, 10 * hydrogen)

  val microcrystallineComponent = Recipe("Microcrystalline component", 1, 30f, 2 * siliconIngot + 1 * copperIngot)
  val circuitBoard = Recipe("Circuit board", 2, 60f, 2 * ironIngot + 1 * copperIngot)
  val processor = Recipe("Processor", 1, 20f, 2 * circuitBoard + 2 * microcrystallineComponent)
  val prism = Recipe("Prism", 2, 30f, 3 * glass)
  val photonCombiner = Recipe("Photon combiner", 1, 20f, 2 * prism + 1 * circuitBoard)
  val steel = Recipe("Steel", 1, 20f, 3 * ironIngot)
  val acid = Recipe("Acid", 4, 10f, 6 * refinedOil + 8 * stone + 4 * water)
  val graphene = Recipe("Graphene", 2, 20f, 3 * energeticGraphite + 1 * acid)
  val solarSail = Recipe("Solar sail", 2, 15f, 1 * graphene + 1 * photonCombiner)
  val titaniumAlloy = Recipe("Titanium alloy", 4, 5f, 4 * titaniumIngot + 4 * steel + 8 * acid)
  val carbonNanotube = Recipe("Carbon nanotube", 2, 15f, 3 * graphene + 1 * titaniumIngot)
  val frameMaterial = Recipe("Frame material", 1, 10f, 4 * carbonNanotube + 1 * titaniumAlloy + 1 * siliconIngot)
  val dysonSphereComponent = Recipe("Dyson sphere component", 1, 7.5f, 3 * frameMaterial + 3 * solarSail + 3 * processor)
  val gear = Recipe("Gear", 1, 60f, 1 * ironIngot)
  val magnet = Recipe("Magnet", 1, 40f, 1 * ironOre)
  val magneticCoil = Recipe("Magnetic coil", 2, 60f, 2 * magnet + 1 * copperIngot)
  val electricMotor = Recipe("Electric motor", 1, 30f, 2 * ironIngot + 1 * gear + 1 * magneticCoil)
  val electromagneticTurbine = Recipe("Electormagnetic turbine", 1, 30f, 2 * electricMotor + 2 * magneticCoil)
  val supermagneticRing = Recipe("Super-magnetic ring", 1, 20f, 2 * electromagneticTurbine + 3 * magnet + 1 * energeticGraphite)
  val deuteronFuelRod = Recipe("Deuteron fuel rod", 2, 5f, 1 * titaniumAlloy + 20 * deuterium + 1 * supermagneticRing)
  val plastic = Recipe("Plastic", 1, 20f, 2 * refinedOil + 1 * energeticGraphite)
  val organicCrystal = Recipe("Organic crystal", 1, 10f, 2 * plastic + 1 * refinedOil + 1 * water)
  val titaniumCrystal = Recipe("Titanium crystal", 1, 15f, 1 * organicCrystal + 3 * titaniumIngot)
  val casimirCrystal = Recipe("Casimir crystal", 1, 15f, 1 * titaniumCrystal + 2 * graphene + 12 * hydrogen)
  val titaniumGlass = Recipe("Titanium glass", 2, 12f, 2 * glass + 2 * titaniumIngot + 2 * water)
  val planeFilter = Recipe("Plane filter", 1, 5f, 1 * casimirCrystal + 2 * titaniumGlass)
  val quantumChip = Recipe("Quantum chip", 1, 10f, 2 * processor + 2 * planeFilter)
  val smallCarrierRocket = Recipe("Small carrier rocker", 1, 10f, 2 * dysonSphereComponent + 4 * deuteronFuelRod + 2 * quantumChip)

  calculateProduction(smallCarrierRocket, smallCarrierRocket.itemsPerRecipe * smallCarrierRocket.recipesPerMinute)
}
