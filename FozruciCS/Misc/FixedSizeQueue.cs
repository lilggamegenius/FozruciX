using System.Collections.Concurrent;
using System.Linq;

namespace FozruciCS.Misc {
    public class FixedSizedQueue <T> : ConcurrentQueue<T> {
        private readonly object syncObject = new object();

        public int Size{ get; }

        public FixedSizedQueue(int size){
            Size = size;
        }

        public new void Enqueue(T obj){
            base.Enqueue(obj);
            lock (syncObject){
                while (Count > Size){
                    T outObj;
                    TryDequeue(out outObj);
                }
            }
        }

        public static implicit operator T(FixedSizedQueue<T> queue)
        {
            return queue.First();
        }
    }
}