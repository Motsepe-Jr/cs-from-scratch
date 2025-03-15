import scala.collection.immutable.{HashMap, ListMap, SortedMap, SortedSet}
import scala.collection.mutable
import scala.collection.mutable.LinkedHashSet
import scala.collection.immutable.Queue

object scala_collections {

  def main(args: Array[String]): Unit = {
    println("Scala collection Demo")
    println("=======================")

    //    setDemo()
    //
    //    mapDemo()

    seqDemo()
  }

  def setDemo(): Unit = {
    // Collections with unique elements
    // a set is collection of distinct elements where order of the elements does not matter,
    // No duplicates {1,1,2} ~ {1,2}
    // {1,2,3} ~ {3,2,1}
    // ordered pair: order matter

    // HashSet - fast lookup, unordered
    // no order, no duplicates, fast lookups, insertions deletion -> 0(1)
    /// how data stored in memory?
    // Each element's hash code determines its bucket
    val hashSet = scala.collection.immutable.HashSet(1, 2, 3, 4, 5)

    /// immutable
    // Cannot change after creation
    // thread safe?
    // val (immutable collection)
    // when a value does not change

    /// mutable
    /// can be modified, add remove elements
    /// var can be reassigned
    /// not thread safe
    /// var when the value needs to change

    // var in Scala is mutable, like let mut in Rust.
    // val in Scala is immutable, like let without mut in Rust.

    println(s"HashSet: $hashSet")
    println(s"Contains 3? ${hashSet.contains(3)}")
    println(s"Contains 6? ${hashSet.contains(6)}")

    /// Why should we care?
    // Hashset fast membership lookups, unique values, set operations, union, and intersection
    // Imagine a dataset with duplicate user IDs. We want to get only unique user IDs.

    def uniqueUserIds(users: List[Int]): Set[Int] = users.toSet

    val usersIds = List(101, 102, 103, 104, 101)
    val uniqueIds = uniqueUserIds(usersIds)
    println(s"Unique User Ids: $uniqueIds")

    // sortedSet - ordered by natural ordering (,5,4,3,2,1) -> (1,2,3,4,5)
    // For Strings (Alphabetical Order - Lexicographical)
    // maintains order based on the natural ordering?
    // Create a custom case class -> Extend Ordered[T] -> Define how elements should compare themselves.

    // memory?
    // SortedSet in scala is implemented using Balanaced Tree Structure (Red-Black Tree
    // it maintains ordering and supports efficient log-time operations for insertion, deletion, and lookups.
    // the tree ensure that the elementes are sorted automatca=iclally withouh requiring explicutsorting
    // basically sortedset are storectured as binary tree

    // Balanced Binary Search trees are performance-wise
    //  good as they provide O(log n) time for search, insert and delete.

    // The absolute difference between heights of left and right subtrees at any node should be less than 1.
    //For each node, its left subtree should be a balanced binary tree.
    //For each node, its right subtree should be a balanced binary tree.

    val sortedSet = SortedSet(5, 3, 1, 2, 4)
    println(s"SortedSet: $sortedSet") // Will print in sorted order

    // data engineers?
    //  Detecting Unique Timestamps of Incoming Events in a Log Stream (Sorted)
    //  We wante to latest timestamps

    val eventLog: SortedSet[Int] = SortedSet(1657890123, 1657890101, 1657890150, 1657890123, 1657890140)

    val newTimestamps = List(1657890160, 1657890105, 1657890123)

    val updatedEventLog = eventLog ++ newTimestamps
    // Add new timestamps while maintaining order and uniqueness
    // no quick sort or merge sort, just log n insert

    println(s"Sorted Unique Event Log: $updatedEventLog")

    // Create a custom case class -> Extend Ordered[T] -> Define how elements should compare themselves.?
    case class Person(name: String, age: Int) extends Ordered[Person] {
      def compare(that: Person): Int = this.age.compare(that.age)
    }

    val people = SortedSet(
      Person("Alice", 30),
      Person("Bob", 25),
      Person("Charlie", 35)
    )

    println(s"Sorted People by Age: $people")

    // BitSet - efficient for integers ( I need to learn )
    // stores non negative integers ->  bitwise representation in memory

    // BitSet internally uses an array of Long values,
    // where each bit in the Long represents whether a number is present.

    // Operations:
    // Membership Check (contains): Uses bitwise AND to check if a bit is set.
    // Insertion (+): Uses bitwise OR to set the bit.
    // Deletion (-): Uses bitwise AND with bit negation.

    // BitSet - efficient for integers
    val bitSet = scala.collection.immutable.BitSet(1, 3, 5, 7, 9)
    // 0000000000000000000000000000000000000000000000000000001010101010 (binary)
    println(s"BitSet: $bitSet")
    println(s"Sum of elements: ${bitSet.sum}")

    // Problem: User Activity Deduplication Using BitSet
    // count user logins per day without using large amount of memory?
    // IDs range from 0 to 10 millions, a SET[INT]

    // Set costing us - 4 bytes per user ID * 10 million -> 40 MB? - > bir set stors only bits this like 32x
    // Assume Int 4 bytes (32 bits)
    //  The set -> hashing and pointers?

    // bitset -> bitwise representation
    //  -> a bit represent whether a number exist or not?
    //  each bit represents whether anumber exist of nor
    // use long to store 64 bits (8 bytes)

    // compare?
    //   val userSet = Set(1, 3, 5) // Normal Set[Int] 3 × 4 = 12 bytes + overhead/pointers ~ 56 bytes?
    //   val bitSet = BitSet(1, 3, 5) // BitSet 8 bytes?
    // Position:   5   4   3   2   1   0
    // BitSet:     1   0   1   0   1   0  (Rest are 0s)

    // LinkedHashSet - preserves insertion order
    // maintains insertion order while ensuring unique elements
    // A Hash Table → For fast lookups, insertions, and deletions (like a regular HashSet).
    // A Doubly Linked List → To maintain the insertion order of elements.

    // // Heads UP:  val does not make the set immutable. It only means that the reference to the set cannot be reassigned.

    // Memory?
    // Each element is stored as a node in a doubly linked list.
      // Head → [5] ⇄ [3] ⇄ [1] ⇄ [2] ⇄ [4] → Tail
        // Where:
          //⇄ represents the doubly linked list connection.  To maintain insertion order.
          //The hash table stores references to these nodes for O(1) lookup.
        // Key -> Value (References to Nodes in Linked List)
        //5   -> [5]
        //3   -> [3]
        //1   -> [1]
        //2   -> [2]
        //4   -> [4]
              // Without a hash table → We would need to scan every node in the list (O(n) time).
              //  With a hash table → We jump directly to 2 in O(1) time.

    // Why learn this anyway? I though I am data engineer??
    // Okay how would you Remove duplicate records from a dataset but keeping the original sequence.
         //  Example of where LinkedHasSet Might be useful: Caching -> Storing a sequence of accessed values where uniqueness and order matter.

    val logs = List(
      "User logged in",
      "Page viewed",
      "User logged in",
      "Button clicked",
      "Page viewed"
    )

    val uniqueLogs = logs.foldLeft(mutable.LinkedHashSet[String]()) { (set, log) => set += log }

    uniqueLogs.foreach(println)

    // TreeSet - based on red-black tree
    val treeSet = scala.collection.immutable.TreeSet(5, 3, 1, 2, 4)
    println(s"TreeSet: $treeSet")
    println(s"First element: ${treeSet.head}")
    println(s"Elements greater than 2: ${treeSet.filter(_ > 2)}")

    // Set operations
    val set1 = Set(1, 2, 3)
    val set2 = Set(3, 4, 5)
    println(s"Union: ${set1.union(set2)}")
    println(s"Intersection: ${set1.intersect(set2)}")
    println(s"Difference: ${set1.diff(set2)}")
  }

  def mapDemo(): Unit = {
    // hashMap
    // HashMap in Scala is a key-value data structure that provides fast lookups, insertions, and deletions.
    // It works by using a hash function to map keys to indexes in an underlying array-based data structure.

    // hash(key) -> interger code value
    // The hash code is used to determine an index in an array of buckets (using modulus operation).

    // When to use?

    val sentences = List(
      "Scala is great",
      "Functional programming in Scala",
      "Scala is powerful"
    )

    val wordCounts = sentences
      .flatMap(_.split(" ")) // flatMap flattens the resulting collections into a single collection. The function returns another collection.
      // .flatMap collects all these arrays and merges them into a single List.
      .foldLeft(HashMap.empty[String, Int]) {
        // fold to process each word, start with empty map
        // check if the map containethe word
        (acc, word) => acc.updated(word, acc.getOrElse(word, 0) + 1)
      }

    println(s"Word Frequency: $wordCounts")

    // SortedMap
      //  immutable map where the keys are stored in sorted order.
      //  Scala’s SortedMap is implemented using a balanced tree structure,
      //

      // how the data is stored?
      // SortedMap maintains key-value pairs in a tree-like structure rather than an array or a hash table.

      // when to use sortedMap
      // Example Problem: Event Log Processing
      // Scenario
      //You are processing a stream of log events that arrive out of order, but you need to process them in chronological order.

    val logs = SortedMap(
      1693500000L -> "User logged in",
      1693500020L -> "User clicked on button",
      1693500010L -> "User viewed profile"
    )

    println("Logs in sorted order:")
    logs.foreach { case (timestamp, message) =>
      println(s"$timestamp -> $message")
    }

    // Inserting a new log event (SortedMap keeps it sorted automatically)
    val updatedLogs = logs + (1693500015L -> "User searched for items")

    println("\nUpdated logs:")
    updatedLogs.foreach { case (timestamp, message) =>
      println(s"$timestamp -> $message")
    }

    // Use it when sorted keys are necessary, such as for time-series or range-based queries.
    // Key Uniqueness	Keys must be unique, but values can repeat	Every element is unique

    //  ListMap
      //  ListMap is an immutable map implementation in Scala that maintains the insertion order of key-value pairs.
      //  Unlike HashMap, which uses a hash table, ListMap is implemented as a linked list of key-value pairs.

      // Internal Storage?
      //    ListMap is a recursive structure where each key -value pair is a node in a linked list.
      //      The list grows from the head meaning new elements are prepended(added at the front).
      //      This makes lookups(get) slower (O(n) complexity) because searching requires scanning the list sequentially .
      //    Insertions are fast only if order is known in advance(O(1) for adding at the front ).
      //    It is best suited for small mappings where preserving order is necessary.

      // We get a list of all the sales product, where each product has a name, and a revenue?

      // we want sort the products by revenue
      // convert the product names to uppercase
      // preserve insertion order ( sorted out data by revenue)

    val salesData = List(
      ("apple", 5000),
      ("banana", 2000),
      ("cherry", 7000),
      ("orange", 1000)
    )

    val sortedSales = salesData.sortBy(-_._2) // descedning order order ( second element of a tuple)

    val transformedSales = sortedSales.map { case (name, revenue) =>
      (name.toUpperCase, revenue)
    }

    val orderedSalesMap = ListMap(transformedSales: _*)

    println(orderedSalesMap)

    // TreeMap - based on red-black tree
    val treeMap = scala.collection.immutable.TreeMap(
      "zebra" -> 26,
      "apple" -> 1,
      "banana" -> 2
    )
    println(s"TreeMap: $treeMap")
    println(s"Keys greater than 'banana': ${treeMap.from("banana").keys.toList}")

    // Map operations

    val hashMap = scala.collection.immutable.HashMap(
      "apple" -> 1,
      "banana" -> 2,
      "cherry" -> 3
    )
    println(s"Keys: ${hashMap.keys}")
    println(s"Values: ${hashMap.values}")
    println(s"Updated map: ${hashMap + ("date" -> 4)}")

      // Wait? if yopu said it is immutable why are you able to update its value?
      // When you update an immutable HashMap, you do not modify the original. Instead, Scala creates a new HashMap, but it is not always built from scratch.
      // Instead, it efficiently reuses parts of the old map to optimize memory and performance.
      // Uses a Trie-based HashMap for structural sharing
  }

  def seqDemo(): Unit = {

    // IndexedSeq
        // indexed sequence that provides fast random access and updates. It is implemented as a 32-way trie (prefix tree),
        // which balances the need for efficient access and modification while maintaining immutability.

        // 32-way trie (also known as a radix tree) is a tree-like data structure where each node contains up to 32 elements (hence the term "32-way").
        // This structure allows efficient indexing, updates, and functional immutability in Scala'

        // if a Vector contains more than 32 elements, it forms a hierarchical tree with multiple levels.
        //Instead of a single large array, elements are split into chunks of size ≤ 32.

    // Logarithmic Depth for Large Sequence
        //The tree has at most 5 levels because:
        //Level 0: Up to 32 elements
        //Level 1: 32² = 1,024 elements // the lvel increase expoentiall by a factor of 32 at each level
        //This structure allows fast O(log₃₂(N)) access and updates, which is nearly O(1) in practical cases.

    // Example:
      //    val index = 99
      //    val chunkSize = 32
      //    val chunkIndex = index / chunkSize // Which chunk to look in
      //    val position = index % chunkSize // Position inside the chunk
      //
      //    println(s"Chunk Index: $chunkIndex")
      //    println(s"Position in Chunk: $position")
      //    println(s"Element at index 99: ${vector(index)}")

    // example usage:

        // Given a log file containing timestamps and event names,
        // transform the data  to:
        //Filter out irrelevant events (e.g., "DEBUG" logs).
        //Transform the logs to a structured format.
        //Find the most common event type.

    val logs: Vector[String] = Vector(
      "INFO: User login successful",
      "DEBUG: Cache refreshed",
      "ERROR: Database connection lost",
      "INFO: Payment processed",
      "DEBUG: User session extended",
      "ERROR: Timeout occurred"
    ) // I am not sure how the strigns are stored? do they use trie? or just indexed?

    val filteredLogs = logs.filterNot(_.startsWith("DEBUG"))

    val structuredLogs = filteredLogs.map { log =>
      val parts = log.split(": ", 2)
      (parts(0), parts(1))
    }

    val eventCounts = structuredLogs.groupBy(_._1).view.mapValues(_.size)
    val mostCommonEvent = eventCounts.maxBy(_._2)

    println(s"Filtered Logs: $filteredLogs")
    println(s"Most Common Event: ${mostCommonEvent._1} occurred ${mostCommonEvent._2} times")

    // NumericRange

        // NumericRange is an efficient way to represent a sequence of numbers in Scala.
        // Internally, it does not store all elements explicitly in memory.
        // Instead, it maintains just a start, end, and step size, computing values lazily when needed.
        // This makes it memory-efficient, especially for large ranges.

        // When you iterate over it (sum, foreach, map),
        // values are generated on demand without storing all of them explicitly.

    // Problem

        // Finding Missing Data Gaps

    val expectedTimestamps = 1 to 100
    val actualTimestamps = Set(1, 2, 3, 5, 7, 10, 11, 12, 15, 20, 50, 100)

    val missing = expectedTimestamps.filterNot(actualTimestamps.contains)

    println(s"Missing timestamps: $missing")

    // Range
      // Similar to NumericRange, Range does not store all elements explicitly. Instead, it maintains:
      // start: 0
      // end (exclusive): 10
      // step: 2
      //This means the numbers in the range are computed lazily when accessed.

      // Data Engineering Use Case: Splitting large Data Into Batches

    val totalRecords = 1000000
    val batchSize = 100000
    val batchIndexes = Range(0, totalRecords, batchSize)

    batchIndexes.foreach { start =>
      val end = (start + batchSize).min(totalRecords)
      println(s"Processing records from $start to ${end - 1}")
    }

    // IndexedSeq:
        // is a trait in Scala that represents sequences where elements can be
        // efficiently accessed by an index.
        // . The key characteristic of IndexedSeq is that it provides fast,
        // constant-time access (O(1)) to elements,

      // How data is store?
         // data it is an indexed collection, meaning it stores elements in a way that allows direct and fast access using an index.

    val str = "Hello"
    val strSeq: IndexedSeq[Char] = str
    println(s"String as IndexedSeq: $strSeq")
    println(s"Character at index 1: ${strSeq(1)}")

    // Problem: Find the First Non-Repeating Character in a String
      val log = "swiss"
      val counts = log.groupBy(identity).view.mapValues(_.length)
      log.find(ch => counts(ch) == 1)

    // List:

      // list in immutable, singly linked list.
      // Each element in a List holds a reference to the next element.
      // The last element (tail) points to Nil, which represents an empty list.
      // Since it's immutable, operations like adding elements return a new list instead of modifying the original.
      // Accessing the head (head) is O(1) (constant time),
      // but accessing elements by index is O(n) (linear time) because we need to traverse the list.
      // Memory representation (linked list nodes): 1 -> 2 -> 3 -> Nil

      // List is best when:
      // You need fast prepend (::) operations (O(1)).
      //    Avoid List if:
      //       You need fast random access(use Vector or Array).

    case class Order(id: Int, amount: Double)

    val orders = List(
      Order(1, 50.0),
      Order(2, 200.0),
      Order(3, 30.0),
      Order(4, 500.0)
    )

    val highValueOrders = orders.filter(_.amount > 100)

    val amounts = highValueOrders.map(_.amount)

    val totalRevenue = amounts.sum

    // (good for prepend, bad for random access) //  sequential processin)

    println(s"Total revenue from high-value orders: $$${totalRevenue}")

    // Queue
    // A Queue is a First-In-First-Out (FIFO) data structure,
    // meaning elements are added at one end (the rear) and removed from the other end (the front).

    // How does this work Internally:

    //    Enqueue(Adding an Element): Inserts an element at the end of the queue.
    //    Dequeue
    //    (Removing an Element): Removes an element from the front of the queue.

    // # Pseudocode of Queue In scala

      //   queue = (front=[], rear=[])
      //
      // # Enqueue operation (Add element to the queue)
      //  function enqueue(queue, element):
      //      queue.rear.append(element)  # Add to rear 5,4,3,2,1
      //      return queue # adding
      //
      // # Dequeue operation (Remove element from front)
      //   function dequeue(queue):
      //      if queue.front is empty:
      //          if queue.rear is empty:
      //              return "Queue is empty"
      //          else:
      //             queue.front = reverse(queue.rear) 1,2,3,4,5
      //             queue.rear = []
      //
      //       element = queue.front[0]   # Get first element, 1
      //       queue.front = queue.front[1:]  # Remove it -> 2,3,4,5
      //       return element, queue



    // Example ofg Queue Usage : Compute the moving average

    case class MovingStats(windowSize: Int, queue: Queue[Double] = Queue(),
                           sum: Double = 0.0, maxVal: Double = Double.MinValue) {

      def next(value: Double): MovingStats = {
        if (queue.length < windowSize) {
          val newQueue = queue.enqueue(value)
          MovingStats(windowSize, newQueue, sum + value, math.max(maxVal, value))
        } else {
          val (oldest, newQueue) = queue.dequeue
          val updatedQueue = newQueue.enqueue(value)
          MovingStats(windowSize, updatedQueue, sum - oldest + value, updatedQueue.max)
        }
      }

      def movingAverage: Double = if (queue.isEmpty) 0.0 else sum / queue.length

      def max: Double = maxVal
    }

    var tracker = MovingStats(3)
    val dataStream = List(5.0, 2.0, 8.0, 6.0, 3.0, 10.0)
    dataStream.foldLeft(tracker) { (t, num) =>
      val newT = t.next(num)
      println(s"Added: $num, Moving Avg: ${newT.movingAverage}, Max: ${newT.max}")
      newT
    }

    // Stack
    // implemented as a linkedin with (Last In First out)
    // head → 1 → 2 → 3 → Nil
    // I push 4: head → 4 → 1 → 2 → 3 → Nil
    // If pop: head → 2 → 3 → Nil

    // example: parsing math expressions ((a+b)*c) undo/redo operatiosn...
  }
}